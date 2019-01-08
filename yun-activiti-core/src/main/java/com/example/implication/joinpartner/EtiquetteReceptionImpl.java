package com.example.implication.joinpartner;

import org.activiti.api.task.runtime.TaskAdminRuntime;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created at 2018/12/28 by Yun
 */

@Service
public class EtiquetteReceptionImpl implements JavaDelegate {

    @Autowired
    TaskService taskService;

    @Autowired
    private TaskAdminRuntime taskAdminRuntime;

    @Autowired
    RepositoryService repositoryService;

    @Autowired
    private RuntimeService runtimeService;

    @Override
    public void execute(DelegateExecution execution) {
        System.out.println(execution.toString());
        execution.setVariable("assignee", "yun");
        execution.setVariable("content", "content yun");

        //runtimeService.startProcessInstanceByKey("myProcess");

        //execution.setTransientVariable("c", "c");

        //ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery().processDefinitionKey(key);
        //ProcessDefinition processDefinition = processDefinitionQuery.singleResult();
        //StartFormData startFormData = formService.getStartFormData(processDefinition.getId());
        //List startFormProperties = startFormData.getFormProperties();
    }
}
