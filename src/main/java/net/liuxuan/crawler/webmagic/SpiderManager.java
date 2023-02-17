package net.liuxuan.crawler.webmagic;

import lombok.extern.slf4j.Slf4j;
import net.liuxuan.crawler.CrawlerDataHolder;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.Task;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Liuxuan
 * @version v1.0.0
 * @description net.liuxuan.crawler.webmagic 包下xxxx工具
 * @date 2023/2/8
 **/
@Slf4j
public class SpiderManager {
    static ConcurrentHashMap<String, Spider> spiderMap = CrawlerDataHolder.getInstance().getSpiderMap();

    public static Task getSpiderById(String uuid) {
        return spiderMap.get(uuid);
    }
}
