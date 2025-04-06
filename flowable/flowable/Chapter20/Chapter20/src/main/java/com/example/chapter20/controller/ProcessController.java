package com.example.chapter20.controller;

import com.example.chapter20.domain.Model;
import com.example.chapter20.engine.form.model.CustomFormModel;
import com.example.chapter20.repository.ModelRepository;
import com.example.chapter20.util.FormModelUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.bpmn.model.ExtensionElement;
import org.flowable.bpmn.model.FlowElement;
import org.flowable.bpmn.model.Process;
import org.flowable.bpmn.model.UserTask;
import org.flowable.common.engine.impl.interceptor.Command;
import org.flowable.common.engine.impl.interceptor.CommandContext;
import org.flowable.engine.FormService;
import org.flowable.engine.HistoryService;
import org.flowable.engine.ManagementService;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.TaskService;
import org.flowable.engine.impl.util.ProcessDefinitionUtil;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.form.api.FormDefinition;
import org.flowable.form.api.FormInfo;
import org.flowable.form.api.FormRepositoryService;
import org.flowable.form.model.FormField;
import org.flowable.form.model.SimpleFormModel;
import org.flowable.task.api.Task;
import org.flowable.task.api.TaskInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
public class ProcessController {
    @Autowired
    private FormService formService;
    @Autowired
    private FormRepositoryService formRepositoryService;
    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private HistoryService historyService;
    @Autowired
    private ManagementService managementService;
    @Autowired
    private ModelRepository modelRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 部署流程
     */
    @GetMapping("/deploy")
    public String deploy() throws Exception {
        String processXml = "CustomFormProcess.bpmn20.xml";
        String formKey = "leave_apply";
        //查找表单模型
        List<Model> result = modelRepository.findModelsByKeyAndType(formKey, Model.MODEL_TYPE_FORM);
        if (result == null || result.size() == 0) {
            return "表单模型" + formKey + "不存在!!";
        }
        log.info("部署流程xml: {}, 表单key: {}", processXml, formKey);
        Model model = result.get(0);
        CustomFormModel customFormModel = FormModelUtil.trans(model);
        SimpleFormModel simpleFormModel = FormModelUtil.trans(customFormModel);
        String formJson = objectMapper.writeValueAsString(simpleFormModel);
        log.info("表单模型Json: {}", formJson);
        String resourceName = "form-" + formKey + ".form";
        //部署流程和表单
        Path path = Paths.get("src/main/resources/processes/" + processXml);
        InputStream inputStream = Files.newInputStream(path);
        Deployment deployment = repositoryService.createDeployment().addInputStream(processXml, inputStream)
                .addString(resourceName, formJson).deploy();
        log.info("部署ID={}", deployment.getId());
        return "部署成功!!";
    }

    /**
     * 发起流程
     */
    @GetMapping("/start")
    public String start() {
        //查询流程定义
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionKey("customFormProcess").latestVersion().singleResult();
        if (processDefinition == null) {
            return "没有找到customFormProcess的流程定义!!";
        }
        //提交表单发起流程
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Map<String, String> startForm = new HashMap<>();
        startForm.put("applicant", "胡海琴");
        startForm.put("apply_time", dateFormat.format(new Date()));
        ProcessInstance processInstance = formService.submitStartFormData(processDefinition.getId(), startForm);
        log.info("发起流程实例: {}", processInstance.getId());
        printCurrentTaskInfo(processInstance.getId());
        return "流程实例发起成功";
    }

    /**
     * 获取任务表单数据
     */
    @GetMapping("/getForm/{taskId}")
    public String getForm(@PathVariable String taskId) throws Exception {
        //查找任务关联的表单
        FormInfo taskFormModel = taskService.getTaskFormModel(taskId);
        SimpleFormModel formModel = (SimpleFormModel) taskFormModel.getFormModel();
        //转换表单模型
        CustomFormModel customFormModel = FormModelUtil.trans(formModel);
        Map<String, CustomFormModel.CustomFormField> fieldMap = customFormModel.getFields();
        //表单变量
        List<FormField> fields = formModel.getFields();
        Map<String, Object> variables = new HashMap<>();
        fields.forEach(field -> {
            variables.put(field.getId(), field.getValue());
        });
        //判断节点是否配置了表单只读属性
        boolean formReadOnly = isFormReadOnly(taskId);
        if (formReadOnly) {
            setReadOnly(fieldMap);
        }
        //构造x-render需要的表单信息
        Map<String, Object> formMap = new HashMap<>();
        formMap.put("type", "object");
        formMap.put("displayType", "row");
        formMap.put("properties", fieldMap);
        //返回表单模型和表单数据
        Map<String, Object> formJSONAndVariableMap = new HashMap<>();
        formJSONAndVariableMap.put("formJson", formMap);
        formJSONAndVariableMap.put("values", variables);
        return objectMapper.writeValueAsString(formJSONAndVariableMap);
    }

    /**
     * 判断节点是否设置了表单只读
     */
    private boolean isFormReadOnly(String taskId) {
        TaskInfo taskInfo = historyService.createHistoricTaskInstanceQuery().taskId(taskId).singleResult();
        //查找流程模型
        return managementService.executeCommand(new Command<Boolean>() {
            @Override
            public Boolean execute(CommandContext commandContext) {
                BpmnModel bpmnModel = ProcessDefinitionUtil.getBpmnModel(taskInfo.getProcessDefinitionId());
                Process process = bpmnModel.getMainProcess();
                Collection<FlowElement> flowElements = process.getFlowElements();
                for (FlowElement flowElement : flowElements) {
                    if (flowElement instanceof UserTask) {
                        UserTask userTask = (UserTask) flowElement;
                        if (userTask.getId().equals(taskInfo.getTaskDefinitionKey())) {
                            Map<String, List<ExtensionElement>> extensionElements = userTask.getExtensionElements();
                            if (extensionElements.containsKey("formReadOnly")) {
                                List<ExtensionElement> extensionElementList = extensionElements.get("formReadOnly");
                                String formReadOnly = extensionElementList.get(0).getElementText();
                                return "true".equalsIgnoreCase(formReadOnly);
                            }
                        }
                    }
                }
                return false;
            }
        });
    }

    /**
     * 设置表单字段为只读状态
     */
    private void setReadOnly(Map<String, CustomFormModel.CustomFormField> fieldMap) {
        for (Map.Entry<String, CustomFormModel.CustomFormField> fieldEntry : fieldMap.entrySet()) {
            CustomFormModel.CustomFormField customFormField = fieldEntry.getValue();
            customFormField.setReadOnly(true);
        }
    }

    /**
     * 办理任务
     */
    @PostMapping("/submit/{taskId}")
    public String submit(@PathVariable String taskId, @RequestBody Map<String, Object> formData) {
        //获取待办任务
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if (task == null) {
            return "任务实例" + taskId + "不存在!!";
        }
        log.info("开始办理任务: 任务ID={}，任务名称={}", taskId, task.getName());
        log.info("表单提交的内容: {}", formData);
        //查找流程定义
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionId(task.getProcessDefinitionId()).singleResult();
        //查找表单定义
        FormDefinition formDefinition = formRepositoryService.createFormDefinitionQuery()
                .parentDeploymentId(processDefinition.getDeploymentId()).formDefinitionKey(task.getFormKey())
                .singleResult();
        //办理任务
        taskService.completeTaskWithForm(taskId, formDefinition.getId(), "agree", formData);
        printCurrentTaskInfo(task.getProcessInstanceId());
        return "办理成功!!";
    }

    private void printCurrentTaskInfo(String processInstanceId) {
        //获取待办任务
        List<Task> taskList = taskService.createTaskQuery().processInstanceId(processInstanceId).list();
        if (taskList != null && taskList.size() > 0) {
            for (Task task : taskList) {
                log.info("==流程实例{}当前节点任务==", processInstanceId);
                log.info("**任务名称:{}", task.getName());
                log.info("**任务ID:{}", task.getId());
            }
        } else {
            log.info("流程实例{}所有节点任务执行完成!", processInstanceId);
        }
    }
}
