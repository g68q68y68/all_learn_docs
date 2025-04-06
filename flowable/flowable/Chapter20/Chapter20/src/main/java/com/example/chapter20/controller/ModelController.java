package com.example.chapter20.controller;

import com.example.chapter20.domain.Model;
import com.example.chapter20.dto.NewModelDto;
import com.example.chapter20.dto.ResponseListDto;
import com.example.chapter20.dto.SaveModelDto;
import com.example.chapter20.repository.ModelRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController
public class ModelController {

    @Autowired
    ModelRepository modelRepository;
    ObjectMapper objectMapper = new ObjectMapper();

    @GetMapping("/list")
    public ResponseListDto<Model> getListByPage(HttpServletRequest request) {
        int current = 1;
        int pageSize = 10;
        try {
            String curStr = request.getParameter("current");
            if (StringUtils.isNotEmpty(curStr)) {
                current = Integer.parseInt(curStr);
            }
            String pageSizeStr = request.getParameter("pageSize");
            if (StringUtils.isNotEmpty(pageSizeStr)) {
                pageSize = Integer.parseInt(pageSizeStr);
            }
        } catch (Exception e) {
            log.warn("parse parameter error!");
        }
        int page = Math.max(0, current - 1);

        ResponseListDto<Model> responseListDto = new ResponseListDto<>();
        long total = modelRepository.count();
        responseListDto.setTotal(total);
        if (total > 0) {
            Page<Model> list = modelRepository.findAll(PageRequest.of(page, pageSize));
            responseListDto.setData(list.getContent());
        }
        return responseListDto;
    }

    @PostMapping("/add")
    public String newModel(@RequestBody NewModelDto newModelDto) throws Exception {
        String modelKey = newModelDto.getModelKey();
        if (StringUtils.isEmpty(modelKey)) {
            throw new Exception("缺少必填内容：表单模型key");
        }
        List<Model> result = modelRepository.findModelsByKeyAndType(modelKey, Model.MODEL_TYPE_FORM);
        if (result != null && result.size() > 0) {
            throw new Exception("重复性校验不通过：表单模型key已存在");
        }
        Model newModel = new Model();
        newModel.setName(newModelDto.getModelName());
        newModel.setKey(newModelDto.getModelKey());
        newModel.setDescription(newModelDto.getDescription());
        newModel.setModelType(Model.MODEL_TYPE_FORM);
        newModel.setCreatedBy("huhaiqin");
        newModel.setLastUpdatedBy("huhaiqin");
        newModel.setModelEditorJson("{}");
        Model model = modelRepository.save(newModel);
        return model.getId();
    }

    @GetMapping("/detail/{modelId}")
    public String getDetail(@PathVariable("modelId") String modelId) throws Exception {
        Optional<Model> result = modelRepository.findById(modelId);
        if (result.isPresent()) {
            Model model = result.get();
            String modelJson = model.getModelEditorJson();
            Map<String, Object> fieldMap = objectMapper.readValue(modelJson,
                    new TypeReference<Map<String, Object>>() {
                    });
            Map<String, Object> formMap = new HashMap<>();
            formMap.put("type", "object");
            formMap.put("displayType", "row");
            formMap.put("properties", fieldMap);
            return objectMapper.writeValueAsString(formMap);
        }
        return "{}";
    }

    @DeleteMapping("/delete/{modelId}")
    public void deleteModel(@PathVariable("modelId") String modelId) {
        modelRepository.deleteById(modelId);
    }

    @PostMapping("/save")
    public String saveModel(@RequestBody SaveModelDto saveModelDto) throws Exception {
        Optional<Model> result = modelRepository.findById(saveModelDto.getModelId());
        if (!result.isPresent()) {
            throw new Exception("表单模型不存在");
        }
        Model model = result.get();
        model.setModelEditorJson(saveModelDto.getModelJson());
        model.setLastUpdatedBy("huhaiqin");
        model.setLastUpdated(new Date());
        modelRepository.save(model);
        return "success";
    }
}
