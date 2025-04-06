package com.example.demo.controller;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

@Data
@Document(collection = "tb_user")
public class User implements Serializable {

    @Id
    private String id;
    private String name;
}
