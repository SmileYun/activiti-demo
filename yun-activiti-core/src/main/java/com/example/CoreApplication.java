package com.example;

import org.activiti.cloud.starter.rb.configuration.ActivitiRuntimeBundle;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

/**
 * Created at 2018/12/26 by Yun
 */
@SpringBootApplication
@ActivitiRuntimeBundle
public class CoreApplication {
    public static ApplicationContext ac;

    /**
     * 与Activiti 7 的Process Runtime API通信需要被授权，用户需要包含ROLE_ACTIVITI_USER角色
     */
    public static void main(String[] args) {
        ac = SpringApplication.run(CoreApplication.class, args);
    }
}
