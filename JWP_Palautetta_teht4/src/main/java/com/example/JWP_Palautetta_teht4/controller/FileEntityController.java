
package com.example.JWP_Palautetta_teht4.controller;

import org.springframework.web.multipart.MultipartFile;

import com.example.JWP_Palautetta_teht4.entity.FileEntity;
import com.example.JWP_Palautetta_teht4.repository.FileEntityRepository;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import org.springframework.http.ResponseEntity;



@CrossOrigin(origins = "http://localhost:8080")
@RestController
@RequestMapping("/files") // Peruspolku kaikille tämän ohjaimen reiteille
public class FileEntityController {

    // Injektoi FileEntityRepository riippuvuusinjektiona konstruktorin kautta
    private final FileEntityRepository fileEntityRepository;

    public FileEntityController(FileEntityRepository fileEntityRepository) {
        this.fileEntityRepository = fileEntityRepository;
    }

    // POST-reitti tiedoston lataamiseen palvelimelle
    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) throws Exception {
        try {

            // Tarkista, onko tiedosto tyhjä
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("File is empty");
            }

            // Tarkista, että tiedosto on kuvatiedosto
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return ResponseEntity.badRequest().body("Only image files are allowed");
            }

            // Tarkista tiedoston koko (raja: 100 MB)
            if (file.getSize() > 104857600) {
                return ResponseEntity.badRequest().body("File is too large");
            }

            // Tarkista, onko tiedosto jo olemassa tietokannassa
            if (fileEntityRepository.findByFileName(file.getOriginalFilename()) != null) {
                return ResponseEntity.badRequest().body("File already exists");
            }

            // Luo uusi tiedosto-olio ja tallenna se tietokantaan
            FileEntity entity = new FileEntity(file.getOriginalFilename(), file.getBytes(), "testUser");
            fileEntityRepository.save(entity);
            return ResponseEntity.ok().body("File uploaded successfully");
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Error");
        }
    }

    @PostMapping("/uploadBuffer")
    public String handleFileUploadWithBuffer(@RequestParam("file") MultipartFile file) throws FileNotFoundException {
        if (!file.isEmpty()) {
            Path filePath = Paths.get(System.getProperty("user.dir")).resolve("upload-dir").resolve(file.getOriginalFilename());
            File saveFile = new File(String.valueOf(filePath));
            try (BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(saveFile))) {
                byte[] bytes = file.getBytes();
                stream.write(bytes);
                return "Uploaded file " + saveFile.getName();
            } catch (IOException e) {
                e.printStackTrace();
                return "You failed to upload " + saveFile.getName() + " => " + e.getMessage();
            }
        } else {
            return "Failed to upload the file.";
        }
    }

    


    // GET-reitti kaikkien tiedostojen listaamiseen
    @GetMapping("/list")
    public List<FileEntity> listFiles() {
        return fileEntityRepository.findAll(); // Hakee ja palauttaa kaikki tiedostot tietokannasta
    }

    @GetMapping("/image/{id}")
    public ResponseEntity<ByteArrayResource> getImageById(@PathVariable String id) throws Exception {
        Optional<FileEntity> fileEntity = fileEntityRepository.findById(id);
        if (fileEntity.isPresent()) {
            FileEntity file = fileEntity.get();
            ByteArrayResource resource = new ByteArrayResource(file.getContent());
    
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + file.getFileName() + "\"");
            
            // Asetetaan oikea MIME-tyyppi kuville (esim. image/jpeg, image/png)
            String contentType = Files.probeContentType(Paths.get(file.getFileName()));
            if (contentType == null || !contentType.startsWith("image/")) {
                contentType = "image/jpeg"; // Oletus, jos tyyppiä ei tunnisteta
            }
    
            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(file.getContent().length)
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(resource);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("")
    public ResponseEntity<ByteArrayResource> downloadFile(@RequestParam String fileName) {
        try{

            FileEntity fileEntity = this.fileEntityRepository.findByFileName(fileName);
            if (fileEntity == null) 
                return ResponseEntity.notFound().build();
            
            ByteArrayResource resource = new ByteArrayResource(fileEntity.getContent());

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileEntity.getFileName() + "\"");
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(fileEntity.getContent().length)
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);
        } catch (Exception e) { 
            e.printStackTrace();
            return ResponseEntity.badRequest().body(null);
        }

    }

    // DELETE-reitti tiedoston poistamiseen tietokannasta tiedoston nimen perusteella@DeleteMapping("/delete/{fileName}")
    @GetMapping("/delete/{fileName}")
    public ResponseEntity<String> deleteFileByUrl(@PathVariable String fileName) {
        FileEntity file = fileEntityRepository.findByFileName(fileName);
        if (file != null) {
            fileEntityRepository.delete(file);
            return ResponseEntity.ok().body("File '" + fileName + "' deleted successfully");
        } else {
            return ResponseEntity.badRequest().body("File '" + fileName + "' not found");
        }
    }

}
