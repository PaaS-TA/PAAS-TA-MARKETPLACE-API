//package org.openpaas.paasta.marketplace.api.config;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.scheduling.annotation.EnableAsync;
//import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
//
//import java.util.concurrent.Executor;
//
///**
// * 비동기 처리 Configuration
// *
// * @author hrjin
// * @version 1.0
// * @since 2019-06-12
// */
//@Configuration
//@EnableAsync
//public class AsyncConfig {
//
//    @Value("${provisioning.pool-size}")
//    private int provisioningPoolSize;
//
//    @Value("${deprovisioning.pool-size}")
//    private int deprovisioningPoolSize;
//
//    @Bean(name = "provisionExecutor")
//    public Executor provisionExecutor() {
//        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
//        executor.setCorePoolSize(provisioningPoolSize);
//        executor.setMaxPoolSize(provisioningPoolSize);
//        executor.setThreadNamePrefix("provision-");
//        executor.initialize();
//        return executor;
//    }
//
//    @Bean(name = "deprovisionExecutor")
//    public Executor deprovisionExecutor() {
//        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
//        executor.setCorePoolSize(deprovisioningPoolSize);
//        executor.setMaxPoolSize(deprovisioningPoolSize);
//        executor.setThreadNamePrefix("deprovision-");
//        executor.initialize();
//        return executor;
//    }
//
//}