//package net.liuxuan.crawler.cron;
//
//import lombok.extern.slf4j.Slf4j;
//import net.liuxuan.crawler.processors.HubTopPageProcessor;
//import net.liuxuan.crawler.utils.scheduler.SpiderRedisScheduler;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.scheduling.annotation.Async;
//import org.springframework.scheduling.annotation.EnableScheduling;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Service;
//import redis.clients.jedis.JedisPool;
//import us.codecraft.webmagic.Spider;
//import us.codecraft.webmagic.pipeline.ConsolePipeline;
//
//import javax.annotation.PostConstruct;
//
///**
// * @author Liuxuan
// * @version v1.0.0
// * @description net.liuxuan.crawler.cron 包下xxxx工具
// * @date 2023/2/1
// **/
//@Service
//@EnableScheduling
//@Slf4j
//public class HubTopCron {
//
//    Spider spider;
//    @Autowired
//    JedisPool jedisPool;
//
//    @Async
////    @Scheduled(fixedRate = 1000 * 60 * 5, initialDelay = 0) //2分钟,延迟0分钟
//    @Scheduled(fixedRate = 100, initialDelay = 0) //2分钟,延迟0分钟
//    public void startHubTop() {
//        if (spider != null) {
//            if (spider.getStatus().equals(Spider.Status.Running)) {
////                spider.addUrl("http://127.0.0."+ RandomUtils.nextInt(0,255));
//            }
//        }
//    }
//
//    @PostConstruct
//    public void init() {
//        HubTopPageProcessor pageProcessor = new HubTopPageProcessor();
//        SpiderRedisScheduler scheduler = new SpiderRedisScheduler(jedisPool);
//        spider = Spider.create(pageProcessor)
//                .addUrl("https://tophub.today/")
//                .addPipeline(new ConsolePipeline())
//                .setScheduler(scheduler)
////                .setScheduler(new QueueScheduler().setDuplicateRemover(new BloomFilterDuplicateRemover(10000000)))
//                .setExitWhenComplete(false)
//                .thread(3);
//
//        spider.runAsync();
//    }
//}
