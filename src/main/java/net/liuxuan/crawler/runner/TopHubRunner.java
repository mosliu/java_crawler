package net.liuxuan.crawler.runner;

import lombok.extern.slf4j.Slf4j;
import net.liuxuan.crawler.CrawlerDataHolder;
import net.liuxuan.crawler.config.CrawlerConfig;
import net.liuxuan.crawler.runner.worker.TopHubWorker;
import net.liuxuan.crawler.runner.worker.WorkerConfig;
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
 * @description net.liuxuan.crawler.runner 包下xxxx工具
 * @date 2023/2/2
 **/
@Component
@Slf4j
@Order(100)
public class TopHubRunner extends OnClosedEventStopAbleRunner {

    @Autowired
    CrawlerConfig config;

    /**
     * @param runnerExecutor
     */
    @Autowired
    @Qualifier("runnerExecutor")
    @Override
    public void setRunnerExecutor(ThreadPoolTaskExecutor runnerExecutor) {
        this.runnerExecutor = runnerExecutor;
    }

    CrawlerDataHolder dataHolder = CrawlerDataHolder.getInstance();

    /**
     * Callback used to run the bean.
     *
     * @param args incoming application arguments
     * @throws Exception on error
     */
    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (!config.isTophubRunnerEnable()) {
            log.info("TopHubRunner is disabled!");
            return;
        }
        WorkerConfig tophubConfig = new WorkerConfig().setScheduleMills(1000 * 60 * 15);
        startThread(TopHubWorker.class, 1, tophubConfig);
//
//        startThread(WechatWorker.class, 1);
    }
}
