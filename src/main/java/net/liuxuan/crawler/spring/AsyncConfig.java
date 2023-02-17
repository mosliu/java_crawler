package net.liuxuan.crawler.spring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * @author Liuxuan
 * @version v1.0.0
 * @description Tools for xx use
 * @date 2019-03-29
 **/
@Configuration
@EnableAsync
public class AsyncConfig implements AsyncConfigurer {

    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        //核心线程数
        taskExecutor.setCorePoolSize(10);
        //最大线程数
        taskExecutor.setMaxPoolSize(1000);
        //队列大小
        taskExecutor.setQueueCapacity(1000);
        taskExecutor.setThreadNamePrefix("main-Executor");
        taskExecutor.initialize();
        return taskExecutor;
    }

    @Bean("runnerExecutor")
    public ThreadPoolTaskExecutor taskExecutor() {
//        Executor asyncExecutor = this.getAsyncExecutor();
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setThreadNamePrefix("Dz-Executor");
        executor.setCorePoolSize(20);
        executor.setMaxPoolSize(1000);
        return executor;
    }

    @Bean("spiderRunnerExecutor")
    public ThreadPoolTaskExecutor taskExecutor2() {
//        Executor asyncExecutor = this.getAsyncExecutor();
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(15);
        executor.setMaxPoolSize(1000);
        executor.setThreadNamePrefix("spider-pool-");
        return executor;
    }


}