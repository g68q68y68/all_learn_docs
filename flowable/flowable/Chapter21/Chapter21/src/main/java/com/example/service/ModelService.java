package com.example.service;

import com.example.entity.Model;
import com.example.repository.ModelRepository;
import org.flowable.engine.ProcessEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;

@Service
public class ModelService {
    @Autowired
    private ModelRepository modelRepository;
    @Autowired
    private ProcessEngine engine;

    @Transactional
    public void saveModel(Model model) {
        String nextId = engine.getProcessEngineConfiguration()
                .getIdGenerator()
                .getNextId();
        model.setId(nextId);
        model.setVersion(1);
        model.setCreated(new Date());
        model.setLastUpdated(new Date());
        modelRepository.save(model);
    }

    public Model getModelById(String id) {
        return modelRepository
                .findById(id)
                .orElseThrow(()->new RuntimeException("流程模型不存在"));
    }
}

