package com.example.controller;

import lombok.extern.slf4j.Slf4j;
import org.activiti.api.process.model.ProcessDefinition;
import org.activiti.api.process.model.ProcessInstance;
import org.activiti.api.process.model.ProcessInstanceMeta;
import org.activiti.api.process.model.builders.ProcessPayloadBuilder;
import org.activiti.api.process.runtime.ProcessRuntime;
import org.activiti.api.runtime.shared.query.Page;
import org.activiti.api.runtime.shared.query.Pageable;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.DeploymentBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * ReST controller to interact with deployed process definitions
 * <p>
 * Created at 2018/12/27 by Yun
 */
@RestController
@Slf4j
public class ProcessController {
    private Logger logger = LoggerFactory.getLogger(ProcessController.class);

    @Autowired
    private ProcessRuntime processRuntime;

    @Autowired
    private ProcessEngine processEngine;

    // @Autowired
    // RuntimeService runtimeService;

    // @Autowired
    // RepositoryService repositoryService;

    @GetMapping("/process-definitions")
    public List<ProcessDefinition> getProcessDefinitions() {
        Page<ProcessDefinition> processDefinitionPage = processRuntime.processDefinitions(Pageable.of(0, 10));


        logger.info("> Available Process definitions: " + processDefinitionPage.getTotalItems());

        for (ProcessDefinition pd : processDefinitionPage.getContent()) {
            logger.info("\t > Process definition: " + pd);
        }
        /*List<ProcessDefinition> processDefinitions =
                repositoryService.createProcessDefinitionQuery().listPage(0, 10);*/

        return processDefinitionPage.getContent();
    }

    @RequestMapping("/start-process")
    public ProcessInstance startProcess(@RequestParam(value = "processDefinitionKey") String processDefinitionKey,
                                        Map<String, Object> variables) {
        Map<String, Object> vars = new HashMap<>();
        vars.put("DEFINITION_VA", "QQ");
        vars.put("DEFINITION_VB", "WW");
        vars.put("DEFINITION_VC", "EE");
        vars.put("appName", "example-yun-job-executor");
        vars.put("jobDesc", "接待处理");
        vars.put("executorParam", "ok");


        ProcessInstance processInstance = processRuntime.start(ProcessPayloadBuilder
                .start()
                .withProcessDefinitionKey(processDefinitionKey)
                .withProcessInstanceName("Sample Process: " + new Date())
                .withVariables(vars)
                .build());
        /*variables.put("employeeName", "yun");
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(processDefinitionKey, variables);
        logger.info(">>> Created Process Instance: " + processInstance);*/

        return processInstance;
    }


    @GetMapping("/process-instances")
    public List<ProcessInstance> getProcessInstances(
            @RequestParam(required = false, defaultValue = "0") String firstResult,
            @RequestParam(required = false, defaultValue = "10") String maxResults) {
        List<ProcessInstance> processInstances =
                processRuntime.processInstances(Pageable.of(Integer.valueOf(firstResult), Integer.valueOf(maxResults))).getContent();

        /*List<ProcessInstance> processInstances = runtimeService.createProcessInstanceQuery()
                .listPage(Integer.valueOf(firstResult), Integer.valueOf(maxResults));*/

        return processInstances;
    }

    @GetMapping("/process-instance-meta")
    public ProcessInstanceMeta getProcessInstanceMeta(
            @RequestParam(value = "processInstanceId") String processInstanceId) {
        ProcessInstanceMeta processInstanceMeta = processRuntime.processInstanceMeta(processInstanceId);
        return processInstanceMeta;
    }



    @GetMapping("/deploy-process")
    public String deployProcess(@RequestParam("name") String name) {
        // 获得一个部署构建器对象，用于加载流程定义文件（test.bpmn.xml test.png）完成流程定义的部署
        DeploymentBuilder builder = processEngine.getRepositoryService().createDeployment();
        //   加载流程定义文件
        builder.addClasspathResource(name);
        //    部署流程定义
        Deployment deployment = builder.deploy();
        log.info(deployment.toString());

        return deployment.toString();
    }
}
