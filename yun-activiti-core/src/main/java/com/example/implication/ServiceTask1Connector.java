package com.example.implication;

/**
 * Created at 2018/12/27 by Yun
 */

import org.activiti.api.process.model.IntegrationContext;
import org.activiti.api.process.runtime.connector.Connector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Implementing Service Tasks and Listeners.
 *
 * Service tasks and listeners are implemented differently in Activiti 7 than in previous versions.
 *
 */
// Implementing the Service Task Spring Bean
@Service(value = "serviceTask1Impl")
public class ServiceTask1Connector implements Connector {
    private Logger logger = LoggerFactory.getLogger(ServiceTask1Connector.class);


    @Override
    public IntegrationContext execute(IntegrationContext integrationContext) {
        logger.info("Some service task logic... [processInstanceId="
                + integrationContext.getProcessInstanceId() + "]");

        Map<String, Object> inForm = integrationContext.getInBoundVariables();
        Map<String, Object> outForm = integrationContext.getOutBoundVariables();

        // return a modified IntegrationContext with the results
        // that needs to be mapped back to process variables.
        return integrationContext;
    }
}
