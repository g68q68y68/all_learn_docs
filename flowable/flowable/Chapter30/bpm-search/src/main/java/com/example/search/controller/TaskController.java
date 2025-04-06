package com.example.search.controller;

import com.example.search.doc.ElasticSearchDocQuery;
import com.example.search.doc.TaskDoc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/search/tasks/")
public class TaskController {

    @Autowired
    private ElasticSearchDocQuery query;

    @GetMapping("/queryByUserId/{userId}")
    public List<TaskDoc> queryByUserId(@PathVariable("userId") String userId) {
        return query.queryTasksByUserId(userId);
    }

}
