package com.example.implication.joinpartner;

import lombok.extern.slf4j.Slf4j;
import org.activiti.api.process.runtime.connector.Connector;
import org.activiti.api.task.runtime.TaskRuntime;
import org.activiti.api.task.runtime.events.TaskAssignedEvent;
import org.activiti.api.task.runtime.events.TaskCompletedEvent;
import org.activiti.api.task.runtime.events.listener.TaskRuntimeEventListener;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.TaskService;
import org.activiti.engine.delegate.ExecutionListener;
import org.activiti.engine.delegate.TaskListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * Created at 2018/12/29 by Yun
 */
@Configuration
@Slf4j
public class TaskListenerConfig {

    @Autowired
    TaskService taskService;

    @Autowired
    ProcessEngine processEngine;

//    @Autowired
//    TaskRuntime taskRuntime;

    @Bean
    public TaskRuntimeEventListener<TaskAssignedEvent> receptionTaskAssignedListener() {
        return taskAssigned -> log.info(">>> Task Assigned: '" + taskAssigned.getEntity()
                .getName() + "' We can send a notification to the assignee: " + taskAssigned.getEntity().getAssignee());
    }

    @Bean
    public TaskRuntimeEventListener<TaskCompletedEvent> receptionTaskCompletedListener() {
        return taskCompleted -> {
            log.info(">>> Task Completed: '" + taskCompleted.getEntity()
                    .getName() + "' We can send a notification to the owner: " + taskCompleted.getEntity().getOwner());
            // taskCompleted.getEntity().getName()
            // taskCompleted.getEntity().getProcessDefinitionId()
            // taskRuntime.setVariables(null);
            //if ( "bid".equals(((TaskCompletedEvent)taskCompleted.getEntity()).getId())) {
                // execution.setVariable("assignee", "yun");
            //    String taskId = ((Task)((TaskCompletedEvent)taskCompleted.getEntity()).getEntity()).getId();
            // taskService.setVariablesLocal(setTaskVariablesPayload.getTaskId(),
            // setTaskVariablesPayload.getVariables());

            String v = (String) taskService.getVariable(taskCompleted.getEntity().getId(), "yunRes");
            taskService.setVariable(taskCompleted.getEntity().getId(), "yunRes", v);
            //}
        };
    }

    @Bean
    public TaskRuntimeEventListener<TaskCompletedEvent> defaultTaskCompletedListener() {
        return taskCompleted -> {
        };
    }

    @Bean
    public TaskListener billTaskListener() {
        return delegateTask -> {
            log.info(">>> Task Completed: '" + delegateTask.getName()
                    + "' We can send a notification to the owner: "
                    + delegateTask.getOwner() + "TaskDefinitionKey" + delegateTask.getTaskDefinitionKey() + "EventName: " + delegateTask.getEventName());
            delegateTask.setVariable("abc", "yun", true);
            //delegateTask.setVariable("cid_abc", "yun", true);
        };
    }

    @Bean
    public ExecutionListener confirmListener() {
        return delegateExecution -> {
            delegateExecution.setVariable("cid_abc", "yun");
            log.info(">>>> " + delegateExecution.toString());
        };
    }

    @Bean
    public ExecutionListener confirmListenerYun() {
        return delegateExecution -> {
            log.info(">>>> " + delegateExecution.toString());
        };
    }

    @Bean
    public Connector etiquetteReceptionConnector() {
        return integrationContext -> {
            Map inBoundVariables = integrationContext.getInBoundVariables();
            String contentToProcess = (String) inBoundVariables.get("content");
            // Logic Here to decide if content is approved or not
            /*if (contentToProcess.contains("??????")) {
                log.info("> Approving content: " + contentToProcess);
                integrationContext.addOutBoundVariable("approved",true);
            } else {
                log.info("> Discarding content: " + contentToProcess);
                integrationContext.addOutBoundVariable("approved",false);
            }*/

            integrationContext.addOutBoundVariable("assignee", "yun");
            integrationContext.addOutBoundVariable("content", "asdasdsadsadsadsadsadsadsdsad yun");
            // xxl-job

            return integrationContext;
        };
    }

    @Bean
    public Connector otherConnector() {
        return integrationContext -> {
            integrationContext.addOutBoundVariable("assignee", "yun1");
            integrationContext.addOutBoundVariable("content", "xxx");
            return integrationContext;
        };
    }

}
