package net.liuxuan.crawler;

import lombok.Data;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.Task;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Liuxuan
 * @version v1.0.0
 * @description Tools for xx use
 * @date 2019-03-29
 **/
@Data
public class CrawlerDataHolder {

    private static CrawlerDataHolder instance = new CrawlerDataHolder();

    private CrawlerDataHolder() {
    }

    public static CrawlerDataHolder getInstance() {
        return instance;
    }


    /**
     * spider的Map
     */
    private final ConcurrentHashMap<String, Spider> spiderMap = new ConcurrentHashMap<>();

    private final ConcurrentHashMap<String, Task> taskMap = new ConcurrentHashMap<>();

}
