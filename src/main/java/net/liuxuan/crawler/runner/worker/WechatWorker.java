package net.liuxuan.crawler.runner.worker;

import lombok.extern.slf4j.Slf4j;
import net.liuxuan.crawler.CrawlerDataHolder;
import net.liuxuan.crawler.webmagic.processor.WeChatPageProcessor;
import net.liuxuan.crawler.spring.runner.worker.StopAbleThread;
import net.liuxuan.crawler.webmagic.scheduler.SpiderRedisScheduler;
import net.liuxuan.crawler.utils.selnium.SpiderSeleniumDownloader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import redis.clients.jedis.JedisPool;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.ConsolePipeline;

import java.io.IOException;

/**
 * @author Liuxuan
 * @version v1.0.0
 * @description net.liuxuan.crawler.runner.worker 包下xxxx工具
 * @date 2023/2/2
 **/
@Slf4j
@Component
public class WechatWorker extends StopAbleThread {

    @Autowired
    JedisPool jedisPool;
    SpiderRedisScheduler scheduler;
    Spider spider;

    public void run() {
        ClassPathResource classPathResource = new ClassPathResource("/selenium.ini");
        String path = null;
        try {
            path = classPathResource.getURL().getPath();
        } catch (IOException e) {
            log.error("未找到文件:{}", "selenium.ini", e);
        }

//        System.setProperty("webdriver.chrome.driver", "E:\\workdocument\\ww\\chromedriver.exe");
//        System.setProperty("selenuim_config", path);
        WeChatPageProcessor pageProcessor = new WeChatPageProcessor();

        scheduler = new SpiderRedisScheduler(jedisPool);

        SpiderSeleniumDownloader downloader = new SpiderSeleniumDownloader();
        spider = Spider.create(pageProcessor)
//                .setDownloader(new SeleniumDownloader("D:\\chromedriver.exe"))
                .setDownloader(downloader)
                .addPipeline(new ConsolePipeline())
                .setScheduler(scheduler)
                .setExitWhenComplete(false)
                .thread(1);
        spider.runAsync();

        //放入spider的Map
        CrawlerDataHolder.getInstance().getSpiderMap().put("wechat", spider);

//        while (!isInterrupted()) {
//            long sleepTime = 1L;
//            if (config != null && config.getScheduleMills() > 0) {
//                sleepTime = config.getScheduleMills();
//            }
//            try {
//                Thread.sleep(sleepTime);
//            } catch (InterruptedException e) {
//                log.info("{} : {} 中断，退出", this.getClass().getName(), this.getName());
//                break;
//            }
//            if (spider != null) {
//                if (scheduler != null) {
//                    scheduler.removeUrl(startUrl);
//                }
//                if (spider.getStatus().equals(Spider.Status.Running)) {
//                    spider.addUrl(startUrl);
//                }
//            }
//        }

    }


    public boolean stopMe() {
        if (spider != null) {
            spider.stop();
        }
        return super.stopMe();
    }
}
