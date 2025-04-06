package com.example.es;

import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Document;

import java.util.Date;

@Data
@Document(indexName = "bpm_task")
public class TaskDoc {
    private String id;
    private String engine;
    private String name;
    private String activityId;
    private String processInstanceId;
    private String processInstanceName;
    private String assignee;
    private String[] candidates;
    private int status;
    private Date createdTime;
    private Date completedTime;
}
