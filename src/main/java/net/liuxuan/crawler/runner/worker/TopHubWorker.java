package net.liuxuan.crawler.runner.worker;

import lombok.extern.slf4j.Slf4j;
import net.liuxuan.crawler.CrawlerDataHolder;
import net.liuxuan.crawler.spring.runner.worker.ArgsStopAbleThread;
import net.liuxuan.crawler.spring.runner.worker.NoArgsException;
import net.liuxuan.crawler.webmagic.processor.TopHubPageProcessor;
import net.liuxuan.crawler.webmagic.scheduler.SpiderRedisScheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.JedisPool;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.ConsolePipeline;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static net.liuxuan.crawler.constansts.TopHubSpiderName.TOPHUB_BILIBILI;

/**
 * @author Liuxuan
 * @version v1.0.0
 * @description net.liuxuan.crawler.runner.worker 包下xxxx工具
 * @date 2023/2/2
 **/
@Slf4j
@Component
public class TopHubWorker extends ArgsStopAbleThread<WorkerConfig> {

    WorkerConfig config = new WorkerConfig();

    /**
     * @param config
     * @throws NoArgsException
     */
    @Override
    public void initArgs(WorkerConfig config) throws NoArgsException {
        this.config = config;
    }


    @Autowired
    JedisPool jedisPool;
    SpiderRedisScheduler scheduler;
    Spider spider;
    String startUrl = "https://tophub.today/";
    List<String> listUrls = new ArrayList<>(Arrays.asList(
            //知乎
            "https://tophub.today/n/mproPpoq6O",
            //微博
            "https://tophub.today/n/KqndgxeLl9",
            //微信
            "https://tophub.today/n/WnBe01o371",
            //百度
            "https://tophub.today/n/Jb0vmloB1G",
            //抖音
            "https://tophub.today/n/DpQvNABoNE",
            //快手
            "https://tophub.today/n/MZd7PrPerO",
            //今日头条
            "https://tophub.today/n/x9ozB4KoXb",
            //B站
            "https://tophub.today/n/74KvxwokxM"

    ));

    @Autowired
    TopHubPageProcessor pageProcessor;

    public void run() {

//        TopHubPageProcessor pageProcessor = new TopHubPageProcessor();

        scheduler = new SpiderRedisScheduler(jedisPool);
        scheduler.addUrlToDuplicateIgnoreList(startUrl);
        listUrls.forEach(e -> scheduler.addUrlToDuplicateIgnoreList(e));
        spider = Spider.create(pageProcessor)
//                .addUrl("https://tophub.today/n/74KvxwokxM")
//                .addUrl("https://tophub.today/")
                .addPipeline(new ConsolePipeline())
                .setScheduler(scheduler)
//                .setScheduler(new QueueScheduler().setDuplicateRemover(new BloomFilterDuplicateRemover(10000000)))
                .setExitWhenComplete(false)
                .thread(3);
        spider.runAsync();

        //放入spider的Map
        CrawlerDataHolder.getInstance().getSpiderMap().put("tophub", spider);

        while (!isInterrupted()) {

            if (spider != null) {
                if (scheduler != null) {
                    scheduler.removeUrl(startUrl);
                    scheduler.removeUrl(startUrl, pageProcessor.getSite().getDomain());
                }
                if (spider.getStatus().equals(Spider.Status.Running)) {
                    spider.addUrl(startUrl);
//                    spider.addRequest(new Request("https://tophub.today/n/74KvxwokxM").putExtra("type", TOPHUB_BILIBILI));
                }
            }

            long sleepTime = 60000L;
            if (config != null && config.getScheduleMills() > 0) {
                sleepTime = config.getScheduleMills();
            }
            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                log.info("{} : {} 中断，退出", this.getClass().getName(), this.getName());
                break;
            }
        }

    }


    public boolean stopMe() {
        if (spider != null) {
            spider.stop();
        }
        return super.stopMe();
    }
}
