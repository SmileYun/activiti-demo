package com.example.implication.joinpartner.connector;

import com.example.implication.joinpartner.model.XxlJobInfo;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONArray;
import org.activiti.cloud.api.process.model.IntegrationRequest;
import org.activiti.cloud.api.process.model.IntegrationResult;
import org.activiti.cloud.connectors.starter.channels.IntegrationResultSender;
import org.activiti.cloud.connectors.starter.configuration.ConnectorProperties;
import org.activiti.cloud.connectors.starter.model.IntegrationResultBuilder;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
        String callServiceRes = doBiz(event);

        // send back message to runtime bundle. Connector实现了与Runtime Bundle的关联
        // 通过流的监听实现的
        Map<String, Object> results = new HashMap<>();
        results.put("var1", var1);
        results.put("assignee", "yun1");
        results.put("content", "xxx");
        results.put("callServiceRes", callServiceRes);
        Message<IntegrationResult> message =
                IntegrationResultBuilder.resultFor(event, connectorProperties).withOutboundVariables(results)
                        .buildMessage();
        integrationResultSender.send(message);
    }

    private String doBiz(IntegrationRequest event) {
        String appName = (String) event.getIntegrationContext().getInBoundVariables().get("appName");
        String jobDesc = (String) event.getIntegrationContext().getInBoundVariables().get("jobDesc");
        String executorParam = (String) event.getIntegrationContext().getInBoundVariables().get("executorParam");

        int taskId = getId(appName, jobDesc);
        return trigger(taskId, executorParam);
    }

    private int getId(String appName, String jobDesc) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        String result = null;

        String url = Const.ADMIN_URL + "/" + Const.QUERY_TASK + "?appName=" + appName + "&jobDesc=" + jobDesc;
        int triggerId = -1;
        try {
            response = httpClient.execute(new HttpGet(url));

            result = EntityUtils.toString(response.getEntity(), "utf-8");

            JSONArray jsonArray = JSONArray.fromObject(result);
            List<XxlJobInfo> xxlJobInfo = (List<XxlJobInfo>) JSONArray.toCollection(jsonArray, XxlJobInfo.class);

            if (xxlJobInfo != null && xxlJobInfo.size() == 1) {
                XxlJobInfo rt = xxlJobInfo.get(0);
                triggerId = rt.getId();
            }
            log.info("返回参数result为:{}", result);
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (response != null) {
                try {
                    EntityUtils.consume(response.getEntity()); // 会自动释放连接
                } catch (IOException e) {
                    log.error("http consume connection fail");
                }
            }
        }
        return triggerId;
    }

    private String trigger(int id, String executorParam) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        String result = null;

        String url = Const.ADMIN_URL + "/" + Const.TRIGGER_TASK;
        HttpPost httpPost = new HttpPost(url);
        List<NameValuePair> list = new ArrayList<>();
        list.add(new BasicNameValuePair("id", String.valueOf(id)));
        list.add(new BasicNameValuePair("executorParam", executorParam));

        httpPost.setEntity(new UrlEncodedFormEntity(list, Charset.forName("UTF-8")));

        try {
            response = httpClient.execute(httpPost);

            result = EntityUtils.toString(response.getEntity(), "utf-8");

            log.info("返回参数result为:{}", result);
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (response != null) {
                try {
                    EntityUtils.consume(response.getEntity()); // 会自动释放连接
                } catch (IOException e) {
                    log.error("http consume connection fail");
                }
            }
        }
        return result;
    }

    public interface YunChannel {
        String ETIQUETTE_CONSUMER = "etiquette";
        String ETIQUETTE_PRODUCER = "etiquette_pro";

        @Input(ETIQUETTE_CONSUMER)
        SubscribableChannel etiquetteReceptionConsumer();

        @Output(ETIQUETTE_PRODUCER)
        MessageChannel etiquetteReceptionProducer();
    }

    class Const {
        public static final String ADMIN_URL = "http://127.0.0.1:8060/xxl-job-admin";
        public static final String TRIGGER_TASK = "jobinfo/trigger";
        public static final String QUERY_TASK = "jobinfo/yunPageList";
    }
}
