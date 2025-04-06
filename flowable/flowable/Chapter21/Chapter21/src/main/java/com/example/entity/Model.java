package com.example.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;

@Data
@Entity(name = "FLW_DE_MODEL")
public class Model {
    @Id
    private String id;
    private String name;
    private String modelKey;
    private String createdBy;
    private Date created;
    private String lastUpdatedBy;
    private Date lastUpdated;
    private Integer version;
    private String modelXml;
    private String tenantId;
}

