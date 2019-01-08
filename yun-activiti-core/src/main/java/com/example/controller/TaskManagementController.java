package com.example.controller;

import lombok.extern.slf4j.Slf4j;
import org.activiti.api.runtime.shared.query.Page;
import org.activiti.api.runtime.shared.query.Pageable;
import org.activiti.api.runtime.shared.security.SecurityManager;
import org.activiti.api.task.model.Task;
import org.activiti.api.task.runtime.TaskAdminRuntime;
import org.activiti.api.task.runtime.TaskRuntime;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.impl.RuntimeServiceImpl;
import org.activiti.engine.runtime.Execution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created at 2018/12/27 by Yun
 */

@RestController
@Slf4j
public class TaskManagementController {
    private Logger logger = LoggerFactory.getLogger(TaskManagementController.class);

    @Autowired
    private TaskRuntime taskRuntime;

    @Autowired
    private TaskAdminRuntime taskAdminRuntime;

    @Autowired
    TaskService taskService;

    @Autowired
    SecurityManager securityManager;

    @Autowired
    RuntimeService runtimeService;

    /**
     * see the User task
     *
     * @return
     */
    @GetMapping("/my-tasks")
    public List<Task> getMyTasks() {
        Page<Task> tasks = taskRuntime.tasks(Pageable.of(0, 10));
        logger.info("> My Available Tasks: " + tasks.getTotalItems());

        for (Task task : tasks.getContent()) {
            logger.info("\t> My User Task: " + task);
        }

        return tasks.getContent();
        // Fetch all tasks for the management group
        /*String assignee = securityManager.getAuthenticatedUserId();
        List<Task> tasks = taskService.createTaskQuery().taskAssignee(assignee).orderByTaskCreateTime().desc().list();
        for (Task task : tasks) {
            log.info("Task available: " + task.getName());
            org.activiti.api.task.model.Task t = new TaskImpl();
        }

        return tasks;*/
    }

    /**
     * This call will require admin credentials.
     * we need to be logged in as a user with ROLE_ACTIVITI_ADMIN.
     *
     * @return
     */
    @GetMapping("/all-tasks")
    public List<Task> getAllTasks() {
        //List<Task> tasks = taskService.createTaskQuery().listPage(0, 10);
        return taskAdminRuntime.tasks(Pageable.of(0, 10)).getContent();
    }

    /**
     * need to be logged in with the user that is assigned the task (so testuser) to complete it.
     * eg. user task assignee testuser, login user is yun, taskRuntime.tasks() will not show up.
     *
     * @param taskId
     * @return
     */
    @RequestMapping("/complete-task")
    public String completeTask(@RequestParam(value="taskId") String taskId, @RequestParam(value="yunRes") String yunRes) {
        Map<String, Object> taskVariables = new HashMap<>();
        taskVariables.put("yunRes", yunRes);
        taskService.complete(taskId, taskVariables);
//        taskRuntime.complete(TaskPayloadBuilder.complete()
//                .withTaskId(taskId).build());
        logger.info(">>> Completed Task: " + taskId);

        return "Completed Task: " + taskId;
    }

    @RequestMapping("/complete-wait-task")
    public String completeWaitTask(@RequestParam(value="processInstanceId") String processInstanceId) {
        Execution execution = runtimeService.createExecutionQuery()
                .processInstanceId(processInstanceId)
                .activityId("confirmrecv")
                .singleResult();
        runtimeService.setVariable(execution.getId(), "assignee", "yun");

        ((RuntimeServiceImpl)runtimeService).signal(execution.getId());

        return "Completed Receive Task: " + execution.getId();
    }
}
