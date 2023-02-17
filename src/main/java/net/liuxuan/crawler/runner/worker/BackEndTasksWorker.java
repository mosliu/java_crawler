package net.liuxuan.crawler.runner.worker;

import lombok.extern.slf4j.Slf4j;
import net.liuxuan.crawler.CrawlerDataHolder;
import net.liuxuan.crawler.entity.feedsdb.JavaCrawlerTasks;
import net.liuxuan.crawler.service.JavaCrawlerTasksService;
import net.liuxuan.crawler.spring.SpringContext;
import net.liuxuan.crawler.spring.runner.worker.StopAbleThread;
import net.liuxuan.crawler.webmagic.CommonSpider;
import net.liuxuan.crawler.webmagic.processor.CommonPageProcessor;
import net.liuxuan.crawler.webmagic.scheduler.SpiderRedisScheduler;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import redis.clients.jedis.JedisPool;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.Pipeline;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * @author Liuxuan
 * @version v1.0.0
 * @description net.liuxuan.crawler.runner.worker 包下xxxx工具
 * @date 2023/2/7
 **/
@Slf4j
@Component
public class BackEndTasksWorker extends StopAbleThread {
    @Autowired
    @Qualifier("spiderRunnerExecutor")
    ThreadPoolTaskExecutor spiderRunnerExecutor;


    @Autowired
    JavaCrawlerTasksService javaCrawlerTasksService;
    @Autowired
    JedisPool jedisPool;

    private static final Integer runFlagRecheckTime = 1000;
    ConcurrentHashMap<String, Spider> spiderMap = CrawlerDataHolder.getInstance().getSpiderMap();

    @Override
    public void run() {
        while (!isInterrupted()) {
            while (isRunFlag()) {
                //加载所有任务
                List<JavaCrawlerTasks> tasks = javaCrawlerTasksService.findAll();
                //没有任务
                if (CollectionUtils.isEmpty(tasks)) {
                    try {
                        log.info("BackendRunner empty job,sleep 10s");
                        Thread.sleep(1000 * 10);
                    } catch (InterruptedException e) {
                        log.info("tasks backend sleep error", e);
                    }
                    continue;
                } else {
                    //每个任务启动
                    tasks.forEach(task -> {
                        //TODO 要检测是否已经存在
                        //检测是否存在
                        String taskName = task.getTaskName();

                        if (spiderMap.get(taskName) != null) {
                            log.info("task {} already running", taskName);
                            return;
                        }

                        //建立processor
                        CommonPageProcessor processor = new CommonPageProcessor();
                        processor.setMetaInfo(task.getSitedomain(), task.getProcessorDomain());

                        SpiderRedisScheduler scheduler = new SpiderRedisScheduler(jedisPool);
                        CommonSpider spider = new CommonSpider(processor);

                        spider.setScheduler(scheduler);
                        spider.setExitWhenComplete(false).thread(1);
//                        spider.setDownloader()
                        String pipelines = task.getPipelines();
                        if (isNotBlank(pipelines)) {
                            String[] pipelineArray = pipelines.split(",");
                            for (String pipeline : pipelineArray) {
                                if (isNotBlank(pipeline)) {
                                    if (pipeline.indexOf('.') < 0) {
                                        pipeline = "net.liuxuan.crawler.webmagic.pipeline." + pipeline;
                                    }
                                    try {
                                        Class<?> aClass = Class.forName(pipeline);
                                        //从spring中加载bean
                                        Object bean = SpringContext.getBean(aClass);
                                        if (bean != null) {
                                            if (bean instanceof Pipeline) {
                                                spider.addPipeline((Pipeline) bean);
                                            }
                                        } else {
                                            if (aClass.isAssignableFrom(Pipeline.class)) {
                                                spider.addPipeline((Pipeline) aClass.newInstance());
                                            }
                                        }
                                    } catch (ClassNotFoundException e) {
                                        log.error("未找到pipeline类：{}", pipeline);
                                    } catch (InstantiationException | IllegalAccessException e) {
                                        log.error("实例化pipeline类：{}失败", pipeline);
                                    }
                                }
                            }
                        }
//                        spider.addPipeline();
                        spider.runAsync();
//                        spider.addUrl("https://www.baidu.com/");

                        spiderMap.put(taskName, spider);
                    });

                }
                try {
                    Thread.sleep(1000 * 60 * 10);
                } catch (InterruptedException e) {
                    log.info("{} : {} 中断，退出", this.getClass().getName(), this.getName());
                    return;
                }
            }
            //即使关掉runflag也不退出进程。
            try {
                Thread.sleep(runFlagRecheckTime);
            } catch (InterruptedException e) {
                log.info("{} : {} 中断，退出", this.getClass().getName(), this.getName());
                return;
            }
        }
    }

    /**
     * @return
     */
    @Override
    public boolean stopMe() {
        return super.stopMe();
    }
}
