package com.example.implication;

import org.activiti.api.model.shared.event.RuntimeEvent;
import org.activiti.api.model.shared.event.VariableCreatedEvent;
import org.activiti.api.process.model.events.SequenceFlowEvent;
import org.activiti.api.process.runtime.events.*;
import org.activiti.api.process.runtime.events.listener.ProcessRuntimeEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Created at 2018/12/27 by Yun
 */

/**
 * Implementing Process Listeners and Task Listeners
 * 流程引擎发送事件，我们以异步的方式监听并订阅事件
 * ProcessRuntimeEventListener实现会自动连接到ProcessRuntime
 * 监听器接收 RuntimeEvent 有关的所有信息
 */

@Service
public class MyProcessEventListener implements ProcessRuntimeEventListener {
    private Logger logger = LoggerFactory.getLogger(MyProcessEventListener.class);


    @Override
    public void onEvent(RuntimeEvent runtimeEvent) {
        if (runtimeEvent instanceof ProcessStartedEvent)
            logger.info("Do something, process is started: " + runtimeEvent.toString());
        else if (runtimeEvent instanceof ProcessCompletedEvent)
            logger.info("Do something, process is completed: " + runtimeEvent.toString());
        else if (runtimeEvent instanceof ProcessCancelledEvent)
            logger.info("Do something, process is cancelled: " + runtimeEvent.toString());
        else if (runtimeEvent instanceof ProcessSuspendedEvent)
            logger.info("Do something, process is suspended: " + runtimeEvent.toString());
        else if (runtimeEvent instanceof ProcessResumedEvent)
            logger.info("Do something, process is resumed: " + runtimeEvent.toString());
        else if (runtimeEvent instanceof ProcessCreatedEvent)
            logger.info("Do something, process is created: " + runtimeEvent.toString());
        else if (runtimeEvent instanceof SequenceFlowEvent)
            logger.info("Do something, sequence flow is taken: " + runtimeEvent.toString());
        else if (runtimeEvent instanceof VariableCreatedEvent)
            logger.info("Do something, variable was created: " + runtimeEvent.toString());
        else
            logger.info("Unknown event: " + runtimeEvent.toString());

    }
}
