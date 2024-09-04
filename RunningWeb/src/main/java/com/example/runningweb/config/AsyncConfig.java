package com.example.runningweb.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
public class AsyncConfig {

    @Bean("commentAsyncExecutors")
    public Executor commentAsyncExecutors(){
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(16);
        executor.setThreadNamePrefix("commentAsyncExe-");
        executor.setQueueCapacity(1024);
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy()); //못보낼 경우 직접 전송하도록
        executor.initialize();
        return executor;
    }

}


