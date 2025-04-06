package com.demo.example.demo1;

import com.demo.example.common.util.FlowableEngineUtil;
import org.flowable.bpmn.model.*;
import org.flowable.engine.repository.DeploymentBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.util.zip.ZipInputStream;

public class DeployDemo extends FlowableEngineUtil {

    @Before
    public void preMethod() {
        //初始化流程引擎
        loadFlowableConfigAndInitEngine("flowable.cfg.xml");
    }

    @After
    public void postMethod() {
        //关闭流程引擎
        closeEngine();
    }

    /**
     * 通过加载输入流的方式进行部署
     */
    /**
     * 通过加载输入流的方式进行部署
     */
    @Test
    public void deployByInputStream() throws IOException {
        //从文件系统读取资源文件，创建输入流
        try (FileInputStream inputStream = new FileInputStream(
                new File("/Users/bpm/processes/HolidayRequest.bpmn20.xml"))) {
            //创建DeploymentBuilder
            DeploymentBuilder builder = repositoryService.createDeployment();
            //将输入流传递给DeploymentBuilder，同时指定资源名称
            builder.addInputStream("HolidayRequest.bpmn20.xml", inputStream);
            //执行部署
            builder.deploy();
        }
    }


    /**
     * 通过加载类路径下的资源文件进行部署
     */
    @Test
    public void deployByClasspathResource() {
        //创建DeploymentBuilder
        DeploymentBuilder builder = repositoryService.createDeployment();
        //加载classpath下的文件
        builder.addClasspathResource("processes/HolidayRequest.bpmn20.xml");
        //执行部署
        builder.deploy();
    }

    /**
     * 通过加载字符串的方式进行部署
     */
    @Test
    public void deployByString() throws IOException {
        //读取文件并转换为string
        try (FileReader fileReader =
                     new FileReader(new File("/Users/bpm/processes/HolidayRequest.bpmn20" +
                             ".xml"));
             BufferedReader bufferedReader = new BufferedReader(fileReader);) {
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
            //创建DeploymentBuilder
            DeploymentBuilder builder = repositoryService.createDeployment();
            //将输入流传递给DeploymentBuilder，同时指定资源名称
            builder.addString("HolidayRequest.bpmn20.xml", stringBuilder.toString());
            //执行部署
            builder.deploy();
        }
    }


    /**
     * 通过加载二进制数据的方式进行部署
     */
    @Test
    public void deployByBytes() throws IOException {
        //读取文件并转换为byte[]
        try (FileInputStream inputStream =
                     new FileInputStream(new File("/Users/bpm/processes/HolidayRequest" +
                             ".bpmn20.xml"));
             ByteArrayOutputStream bos = new ByteArrayOutputStream();) {
            byte[] temp = new byte[1024];
            int n;
            while ((n = inputStream.read(temp)) != -1) {
                bos.write(temp, 0, n);
            }
            //创建DeploymentBuilder
            DeploymentBuilder builder = repositoryService.createDeployment();
            //将字节数组传递给DeploymentBuilder，同时指定资源名称
            builder.addBytes("HolidayRequest.bpmn20.xml", bos.toByteArray());
            //执行部署
            builder.deploy();
        }
    }

    /**
     * 通过加载压缩包的方式进行部署
     */
    @Test
    public void deployByZipInputStream() throws IOException {
        //读取ZIP文件
        try (FileInputStream inputStream =
                     new FileInputStream(new File("/Users/bpm/processes/HolidayRequest.zip"));
             ZipInputStream zipInputStream = new ZipInputStream(inputStream);) {
            //创建DeploymentBuilder
            DeploymentBuilder builder = repositoryService.createDeployment();
            //传递ZIP输入流给DeploymentBuilder
            builder.addZipInputStream(zipInputStream);
            //执行部署
            builder.deploy();
        }
    }

    /**
     * 通过加载BpmnModel的方式进行部署
     */
    @Test
    public void deployByBpmnModel() {
        //创建BpmnModel对象
        BpmnModel model = new BpmnModel();
        //创建流程(Process)
        org.flowable.bpmn.model.Process process = new org.flowable.bpmn.model.Process();
        model.addProcess(process);
        process.setId("HolidayRequest");
        process.setName("请假申请流程");
        //创建开始节点
        StartEvent startEvent = new StartEvent();
        startEvent.setId("startEvent1");
        startEvent.setName("开始");
        process.addFlowElement(startEvent);
        //创建任务节点
        UserTask userTask1 = new UserTask();
        userTask1.setId("userTask1");
        userTask1.setName("申请");
        process.addFlowElement(userTask1);
        //创建任务节点
        UserTask userTask2 = new UserTask();
        userTask2.setId("userTask2");
        userTask2.setName("审批");
        process.addFlowElement(userTask2);
        //创建结束节点
        EndEvent endEvent = new EndEvent();
        endEvent.setId("endEvent1");
        endEvent.setName("结束");
        process.addFlowElement(endEvent);
        //创建节点的关联关系
        process.addFlowElement(new SequenceFlow("startEvent1", "userTask1"));
        process.addFlowElement(new SequenceFlow("userTask1", "userTask2"));
        process.addFlowElement(new SequenceFlow("userTask2", "endEvent1"));
        //创建DeploymentBuilder
        DeploymentBuilder builder = repositoryService.createDeployment();
        //传递BpmnModel给DeploymentBuilder
        builder.addBpmnModel("HolidayRequest.bpmn20.xml", model);
        //执行部署
        builder.deploy();
    }

}
