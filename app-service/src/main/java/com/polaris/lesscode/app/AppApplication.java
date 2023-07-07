package com.polaris.lesscode.app;

import io.sentry.Sentry;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableSwagger2
@EnableFeignClients(basePackages = {
        "com.polaris.lesscode.form.internal.feign",
        "com.polaris.lesscode.gotable.internal.feign",
        "com.polaris.lesscode.gopush.internal.feign",
		"com.polaris.lesscode.agg.internal.feign",
        "com.polaris.lesscode.permission.internal.feign",
		"edp.davinci.internal.feign",
        "com.polaris.lesscode.workflow.internal.feign",
        "com.polaris.lesscode.project.internal.feign",
        "com.polaris.lesscode.uc.internal.feign",
        "com.polaris.lesscode.msgsvc.internal.feign",
        "com.polaris.lesscode.dc.internal.feign"})
@EnableAspectJAutoProxy(proxyTargetClass = true)
@SpringBootApplication
public class AppApplication {

    public static void main(String[] args) {
        String env = System.getenv("SERVER_ENVIROMENT");
        Sentry.getStoredClient().setEnvironment(env);
        SpringApplication.run(AppApplication.class, args);
    }
}
