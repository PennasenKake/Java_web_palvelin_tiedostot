package com.example.JWP_Palautetta_teht4.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.example.JWP_Palautetta_teht4.entity.FileEntity;

public interface FileEntityRepository extends MongoRepository<FileEntity, String> {

    FileEntity findByFileName(String fileName);


    
}