package com.example.implication;

import org.activiti.api.model.shared.event.RuntimeEvent;
import org.activiti.api.task.model.Task;
import org.activiti.api.task.model.impl.TaskImpl;
import org.activiti.api.task.runtime.events.*;
import org.activiti.api.task.runtime.events.listener.TaskRuntimeEventListener;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.TaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created at 2018/12/27 by Yun
 */

@Service
public class MyTaskEventListener implements TaskRuntimeEventListener {

    private Logger logger = LoggerFactory.getLogger(MyTaskEventListener.class);

    @Autowired
    TaskService taskService;

    @Autowired
    ProcessEngine processEngine;

    @Override
    public void onEvent(RuntimeEvent runtimeEvent) {

        if (runtimeEvent instanceof TaskActivatedEvent) {
            logger.info("Do something, task is activated: " + runtimeEvent.toString());

        }
        else if (runtimeEvent instanceof TaskAssignedEvent) {
            TaskAssignedEvent taskEvent = (TaskAssignedEvent) runtimeEvent;
            Task task = taskEvent.getEntity();
            logger.info("Do something, task is assigned: " + task.toString());
        } else if (runtimeEvent instanceof TaskCancelledEvent)
            logger.info("Do something, task is cancelled: " + runtimeEvent.toString());
        else if (runtimeEvent instanceof TaskCompletedEvent)
            logger.info("Do something, task is completed: " + runtimeEvent.toString());
        else if (runtimeEvent instanceof TaskCreatedEvent) {
            logger.info("Do something, task is created: " + runtimeEvent.toString());
            if ( "bid".equals(((TaskImpl)runtimeEvent.getEntity()).getName())) {
                // execution.setVariable("assignee", "yun");
                String taskId = ((TaskImpl)runtimeEvent.getEntity()).getId();
                taskService.setVariable(taskId, "abc", "yun");
            }
        }
        else if (runtimeEvent instanceof TaskSuspendedEvent)
            logger.info("Do something, task is suspended: " + runtimeEvent.toString());
        else
            logger.info("Unknown event: " + runtimeEvent.toString());

    }
}
