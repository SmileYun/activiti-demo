package com.example.implication.joinpartner.connector;

import lombok.extern.slf4j.Slf4j;
import org.activiti.cloud.api.process.model.IntegrationRequest;
import org.activiti.cloud.api.process.model.IntegrationResult;
import org.activiti.cloud.connectors.starter.channels.IntegrationResultSender;
import org.activiti.cloud.connectors.starter.configuration.ConnectorProperties;
import org.activiti.cloud.connectors.starter.model.IntegrationResultBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;

import java.util.HashMap;
import java.util.Map;


/**
 * Created at 2019/01/03 by Yun
 */
@Slf4j
@EnableBinding(EtiquetteReceptionReceiver.YunChannel.class)
public class EtiquetteReceptionReceiver {

    private IntegrationResultSender integrationResultSender;

    // connector的信息，例如：service type，version，application name， version
    @Autowired
    private ConnectorProperties connectorProperties;


    public EtiquetteReceptionReceiver(IntegrationResultSender integrationResultSender) {
        this.integrationResultSender = integrationResultSender;
    }

    @StreamListener(value = YunChannel.ETIQUETTE_CONSUMER)
    public void execute(IntegrationRequest event) throws InterruptedException {

        String var1 = EtiquetteReceptionReceiver.class.getSimpleName() + " was called for instance " + event
                .getIntegrationContext().getProcessInstanceId();

        // Implement your business logic here
        // ...
        // event.getIntegrationContext().getInBoundVariables()
        //String callServiceRes = doBiz(event);

        // send back message to runtime bundle. Connector实现了与Runtime Bundle的关联
        Map<String, Object> results = new HashMap<>();
        results.put("var1", var1);
        results.put("assignee", "yun1");
        results.put("content", "xxx");
        Message<IntegrationResult> message =
                IntegrationResultBuilder.resultFor(event, connectorProperties).withOutboundVariables(results)
                        .buildMessage();
        integrationResultSender.send(message);
    }


    public interface YunChannel {
        String ETIQUETTE_CONSUMER = "etiquette";
        String ETIQUETTE_PRODUCER = "etiquette_pro";

        @Input(ETIQUETTE_CONSUMER)
        SubscribableChannel etiquetteReceptionConsumer();

        @Output(ETIQUETTE_PRODUCER)
        MessageChannel etiquetteReceptionProducer();
    }
}
