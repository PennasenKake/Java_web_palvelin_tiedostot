package com.example.JWP_Palautetta_teht4.entity;

// import jakarta.persistence.Column;
// import jakarta.persistence.Entity;
// import jakarta.persistence.GeneratedValue;
// import jakarta.persistence.GenerationType;
// import jakarta.persistence.Id;
// import jakarta.persistence.Lob;
// import jakarta.persistence.Table;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;


//import org.springframework.data.mongodb.core.mapping.Document;


// @Entity
// @Table(name = "files")

// Määrittelee tämän luokan MongoDB-kokoelman "files" dokumentiksi
@Document(collection = "files")
public class FileEntity {

    @Id // MongoDB:n yksilöivä tunniste (ObjectId tallennetaan String-muodossa)
    // @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    // @Column(name = "file_name", unique = true)
    private String fileName;

    // @Lob
    // @Column(columnDefinition = "LONGBLOB")
    private byte[] content;

    private String owner;

    // Parametrillinen konstruktori tiedoston luomiseen
    public FileEntity(String fileName, byte[] content, String owner) {
        this.fileName = fileName;
        this.content = content;
        this.owner = owner;
    }

    public FileEntity() {}

    
    // getters and setters

    public String getId() { return id; }
    
    public void setId(String id) { this.id = id; }

    public String getFileName() { return fileName; }

    public void setFileName(String fileName) { this.fileName = fileName;}

    public byte[] getContent() { return content;}

    public void setContent(byte[] content) { this.content = content;}

    public String getOwner() { return owner;}

    public void setOwner(String owner) { this.owner = owner;}

    
}
