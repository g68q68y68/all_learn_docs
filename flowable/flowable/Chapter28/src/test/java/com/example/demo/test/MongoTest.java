package com.example.demo.test;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;

@SpringBootTest
public class MongoTest {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Test
    public void testCreateCollection(){
        boolean exists = mongoTemplate.collectionExists("variable");
        if (!exists){
            mongoTemplate.createCollection("variable");
        }
    }

}
