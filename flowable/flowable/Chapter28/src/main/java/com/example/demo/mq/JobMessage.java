package com.example.demo.mq;

import lombok.Data;

@Data
public class JobMessage {
    private String processInstanceId;
    private String jobId;

    public JobMessage() {
    }
    public JobMessage(String processInstanceId, String jobId) {
        this.processInstanceId = processInstanceId;
        this.jobId = jobId;
    }
}
