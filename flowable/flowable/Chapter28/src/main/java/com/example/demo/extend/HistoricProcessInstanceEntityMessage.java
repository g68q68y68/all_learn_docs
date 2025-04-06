package com.example.demo.extend;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.flowable.engine.impl.persistence.entity.HistoricProcessInstanceEntity;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HistoricProcessInstanceEntityMessage {
    private OpType opType;
    private HistoricProcessInstanceEntity entity;
    private Long currentTime;
}
