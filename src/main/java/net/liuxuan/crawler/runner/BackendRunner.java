package net.liuxuan.crawler.runner;

import lombok.extern.slf4j.Slf4j;
import net.liuxuan.crawler.config.CrawlerConfig;
import net.liuxuan.crawler.runner.worker.BackEndTasksWorker;
import net.liuxuan.crawler.spring.runner.OnClosedEventStopAbleRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.ApplicationArguments;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

/**
 * @author Liuxuan
 * @version v1.0.0
 * @description 根据数据库的后台任务运行器
 * @date 2023/2/7
 **/
@Component
@Slf4j
@Order(100)
public class BackendRunner extends OnClosedEventStopAbleRunner {

    @Autowired
    CrawlerConfig config;

    /**
     * @param runnerExecutor
     */
    @Autowired
    @Qualifier("spiderRunnerExecutor")
    @Override
    public void setRunnerExecutor(ThreadPoolTaskExecutor runnerExecutor) {
        this.runnerExecutor = runnerExecutor;
    }


    /**
     * Callback used to run the bean.
     *
     * @param args incoming application arguments
     * @throws Exception on error
     */
    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (!config.isBackendRunnerEnable()) {
            log.info("BackendRunner is disabled!");
            return;
        }
        startThread(BackEndTasksWorker.class, 1);
    }

}
