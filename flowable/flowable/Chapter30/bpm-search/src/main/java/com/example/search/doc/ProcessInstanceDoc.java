package com.example.search.doc;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ProcessInstanceDoc extends BaseDoc{
    private String id;
    private String processInstanceName;
    private String starterUser;
    private String status;
    private LocalDateTime createdTime;
    private LocalDateTime endedTime;
}
