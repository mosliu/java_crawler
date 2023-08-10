package net.liuxuan.crawler.webmagic.processor;

import lombok.extern.slf4j.Slf4j;
import net.liuxuan.crawler.CrawlerDataHolder;
import net.liuxuan.crawler.entity.HubTopEntity;
import net.liuxuan.crawler.entity.feedsdb.JavaCrawlerHotPointContent;
import net.liuxuan.crawler.entity.feedsdb.JavaCrawlerTasks;
import net.liuxuan.crawler.service.JavaCrawlerHotPointContentService;
import net.liuxuan.crawler.service.JavaCrawlerTasksService;
import net.liuxuan.crawler.utils.EncryptUtil;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Selectable;

import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static net.liuxuan.crawler.constansts.TaskMapType.TopHubTaskType;
import static net.liuxuan.crawler.constansts.TopHubSpiderName.TOPHUB_JINGJIGUANCHA;
import static net.liuxuan.crawler.constansts.TopHubSpiderName.TOPHUB_XINLANGCAIJING;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * @author Liuxuan
 * @version v1.0.0
 * @description net.liuxuan.crawler hubtop.today爬取
 * @date 2023/1/28
 **/
@Slf4j
@Component
public class TopHubPageProcessor implements PageProcessor {
    // 部分一：抓取网站的相关配置，包括编码、抓取间隔、重试次数等
    private Site site;

    public static final String TOPHUBCOOKIEKEY = "tophub_cookie_key";
    public static final String topHubUrlRegex = "https://tophub\\.today/*";
    //https://tophub.today/n/x9ozqX7eXb
    public static final String topHubSingleListUrlRegex = "https://tophub\\.today/n/*";
    //详情页url
    private static String pageUrl = "https://bj\\.fang\\.ke\\.com/loupan/p_\\w+/";
    //最里面的详细信息url
    private static String detailedPageUrl = "https://bj\\.fang\\.ke\\.com/loupan/p_\\w+/xiangqing/";

    @Autowired
    @Qualifier("redis113RedisTemplate")
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    JavaCrawlerHotPointContentService javaCrawlerHotPointContentService;

    @Autowired
    JavaCrawlerTasksService javaCrawlerTasksService;

    List<JavaCrawlerTasks> hubtopTasks = new ArrayList<>();
    long lastGetTaskTime = 0L;

    // process是定制爬虫逻辑的核心接口，在这里编写抽取逻辑
    @Override
    public void process(Page page) {

        log.info("process url:{}", page.getUrl());
        LocalDateTime now = LocalDateTime.now();
        getHubTopTasks();
        //判断是否为列表的url
        if ((page.getUrl().regex(topHubSingleListUrlRegex).match())) {

            Map<String, Object> extras = page.getRequest().getExtras();
            String type = (String) extras.get("type");
            // //*[@id="tabbed-header-panel"]/div[2]/div[1]
            //获取类型
            String className = page.getHtml().xpath("//*[@id=\"tabbed-header-panel\"]/div[2]/div[1]").$("div", "text").get();

            List<Selectable> nodes = page.getHtml().$(".table tr").nodes();
            List<HubTopEntity> entities = new ArrayList<>();
            nodes.forEach(node -> {
                HubTopEntity entity = new HubTopEntity();
                //这个是每一行tr有4个td，第一个是序号，第二个是标题，第三个是热度，第四个是链接
                String id = node.$("td:nth-child(1)", "text").get();
                if (isNotBlank(id)) {
                    id = id.replaceAll("\\.", "").trim();
                }
                String url = node.$("td a", "href").get();
                String title = node.$("td:nth-child(2) a", "text").get();
                String hotValue = node.$("td:nth-child(3)", "text").get();


                String itemid = node.$("td:nth-child(2) a", "itemid").get();
                entity.setTitle(title).setHotValue(hotValue).setUrl(url).setItemid(itemid).setSortid(id);
                entities.add(entity);
            });
            saveAndAddUrlsToSpider(type, now, entities);
            page.putField(type, entities);
        } else if (page.getUrl().regex(topHubUrlRegex).match()) {

//        //最终的url要shuffle以后再采集，有利于反爬
//        List<String> urls = new ArrayList<>();


//
//            //获取微博
//            String weiboXpath = "//*[@id=\"node-1\"]/div/div[2]/div[1]/a";
//            //微博热搜
//            findListAndProcess(page, now, weiboXpath, TOPHUB_WEIBO);
//
//            //知乎
//            // //*[@id="node-6"]/div/div[2]/div[1]/a
//            String zhihuXpath = "//*[@id=\"node-6\"]/div/div[2]/div[1]/a";
//            findListAndProcess(page, now, zhihuXpath, TOPHUB_ZHIHU);
//
//            // wechat
//            //  //*[@id="node-5"]/div/div[2]/div[1]/a
//            String wechatXpath = "//*[@id=\"node-5\"]/div/div[2]/div[1]/a";
//            findListAndProcess(page, now, wechatXpath, TOPHUB_WECHAT);
//            // baidu
//            // //*[@id="node-2"]/div/div[2]/div[1]/a[1]
//            String baiduXpath = "//*[@id=\"node-2\"]/div/div[2]/div[1]/a";
//            findListAndProcess(page, now, baiduXpath, TOPHUB_BAIDU);
//            // bilibili
//            // //*[@id="node-19"]/div/div[2]/div[1]/a[1]/div/span[1]
//            String bilibiliXpath = "//*[@id=\"node-19\"]/div/div[2]/div[1]/a";
//            findListAndProcess(page, now, bilibiliXpath, TOPHUB_BILIBILI);
//            // 头条
//            String toutiaoXpath = "//*[@id=\"node-3608\"]/div/div[2]/div[1]/a";
//            findListAndProcess(page, now, toutiaoXpath, TOPHUB_TOUTIAO);
//            // 快手
//            String kuaishouXpath = "//*[@id=\"node-26325\"]/div/div[2]/div[1]/a";
//            findListAndProcess(page, now, kuaishouXpath, TOPHUB_KUAISHOU);
//            // 抖音
//            String douyinXpath = "//*[@id=\"node-221\"]/div/div[2]/div[1]/a";
//            findListAndProcess(page, now, douyinXpath, TOPHUB_DOUYIN);
            // 澎湃
//            String PengpaiXpath = "//*[@id=\"node-51\"]/div/div[2]/div[1]/a";
//            findListAndProcess(page, now, PengpaiXpath, TOPHUB_PENGPAI);
            //新浪国内新闻
//            String xinLangXpath = "//*[@id=\"node-246\"]/div/div[2]/div[1]/a";
//            findListAndProcess(page, now, xinLangXpath, TOPHUB_XINLANG);
//            String jingjiguanchaXpath = "//*[@id=\"node-22185\"]/div/div[2]/div[1]/a";
//            findListAndProcess(page, now, jingjiguanchaXpath, TOPHUB_JINGJIGUANCHA);
//            String xinLangCaiJingXpath = "//*[@id=\"node-32273\"]/div/div[2]/div[1]/a";
//            findListAndProcess(page, now, xinLangCaiJingXpath, TOPHUB_XINLANGCAIJING);

            for (JavaCrawlerTasks hubtopTask : hubtopTasks) {
                String xpath = "//*[@id=\"node-" + hubtopTask.getExtraInfoInt1() + "\"]/div/div[2]/div[1]/a";
                findListAndProcess(page, now, xpath, hubtopTask.getTaskName());
            }
        }


    }

    private void getHubTopTasks() {
        if (hubtopTasks == null) {
            hubtopTasks = new ArrayList<>();
        }
        if (hubtopTasks.size() == 0) {
            hubtopTasks = javaCrawlerTasksService.findAll(new JavaCrawlerTasks().setActive(true).setTaskType(TopHubTaskType.id()));
            lastGetTaskTime = System.currentTimeMillis();
            return;
        }
        if ((System.currentTimeMillis() - lastGetTaskTime) > 1000 * 60 - 5) {
            hubtopTasks = javaCrawlerTasksService.findAll(new JavaCrawlerTasks().setActive(true).setTaskType(TopHubTaskType.id()));
            lastGetTaskTime = System.currentTimeMillis();
        }
    }

    /**
     * 从page中获取HubTopEntity 并保存到数据库 以及加入到spider中
     *
     * @param page
     * @param now
     * @param weiboXpath
     * @param spiderName
     */
    private void findListAndProcess(Page page, LocalDateTime now, String weiboXpath, String spiderName) {
        List<HubTopEntity> weiBoEntitys = getHubTopEntities(page, weiboXpath);
        saveAndAddUrlsToSpider(spiderName, now, weiBoEntitys);
        page.putField(spiderName, weiBoEntitys);
    }

    /**
     * 从map中获取相应的spider并吧url加入进去
     *
     * @param spiderName
     * @param hubTopEntitys
     */
    private void saveAndAddUrlsToSpider(String spiderName, LocalDateTime dt, List<HubTopEntity> hubTopEntitys) {

        hubTopEntitys.forEach(e -> {
            JavaCrawlerHotPointContent content = new JavaCrawlerHotPointContent();
            String gid = "" + dt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
            String url = e.getUrl();
            String urlMd5 = EncryptUtil.MD5(url);

            content.setTitle(e.getTitle())
                    .setType(spiderName)
                    .setSortNum(e.getSortid())
                    .setHotNum(e.getHotValue())
                    .setUrl(url)
                    .setUrlMd5(urlMd5)
                    .setStatus(0)
                    .setGid(gid)
                    .setAddTime(LocalDateTime.now())
            ;


            //从库里面查找 MD5相同的数据
            List<JavaCrawlerHotPointContent> all = javaCrawlerHotPointContentService.findAll(new JavaCrawlerHotPointContent().setUrlMd5(urlMd5));
            if (all.size() > 0) {
                //数据存在
                Optional<JavaCrawlerHotPointContent> first = all.stream().filter(c -> c.getStatus() == 1).findFirst();
                if (first.isPresent()) {
                    //有已经抓取的数据
                    //该条不抓了
                    e.setNeedCrawl(false);
                    List<JavaCrawlerHotPointContent> collect = all.stream().filter(d -> d.getStatus() == 0).collect(Collectors.toList());
                    collect.add(content);
                    collect.forEach(d -> {
                        d.setStatus(1);
                        d.setContent(first.get().getContent());
                        d.setDesc(first.get().getDesc());
                        javaCrawlerHotPointContentService.save(d);
                    });
                    return;
                }
                //获取原始的信息，填入到新的对象中

            }


            javaCrawlerHotPointContentService.save(content);
        });

        ConcurrentHashMap<String, Spider> spiderMap = CrawlerDataHolder.getInstance().getSpiderMap();
        if (spiderMap != null) {
            Spider spiderGot = spiderMap.get(spiderName);
            if (spiderGot != null && spiderGot.getStatus().equals(Spider.Status.Running)) {
                hubTopEntitys.forEach(e -> {
                    if (e != null && e.getUrl() != null) {
                        if (!e.isNeedCrawl()) {
                            log.info("{} 的数据不需要抓取", e.getUrl());
                            return;
                        }
                        Map<String, Object> extraParams = new HashMap<>();

                        try {
                            BeanUtils.populate(e, extraParams);
                        } catch (IllegalAccessException | InvocationTargetException ex) {
                            log.error("populate error", ex);
                        }
                        Request request = new Request(e.getUrl());
                        if (extraParams != null && extraParams.size() > 0) {
                            extraParams.forEach((k, v) -> {
                                request.putExtra(k, v);
                            });
                        }
                        request.putExtra("type", spiderName);

                        spiderGot.addRequest(request);
                        spiderGot.addUrl(e.getUrl());
                    }
                });

            }
        }
    }


    private static List<HubTopEntity> getHubTopEntities(Page page, String weiboXpath) {
        List<Selectable> nodes = page.getHtml().xpath(weiboXpath).nodes();
        List<HubTopEntity> hubTopEntities = new ArrayList<>();
        nodes.forEach(node -> {
            HubTopEntity entity = new HubTopEntity();
            String id = node.$("div span.s", "text").get();
            if (isBlank(id)) {
                id = node.$("div span.s.h", "text").get();
            }
            String title = node.$("div span.t", "text").get();
            String hotValue = node.$("div span.e", "text").get();
            String url = node.$("a", "href").get();
            String itemid = node.$("a", "itemid").get();
            entity.setTitle(title).setHotValue(hotValue).setUrl(url).setItemid(itemid).setSortid(id);
            hubTopEntities.add(entity);
        });
        return hubTopEntities;
    }

    @Override
    public Site getSite() {

        if (site == null) {
            site = Site.me().setDomain("tophub.today")
                    .setUserAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_2) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.65 Safari/537.31")
//                    .addCookie("RegisteredUserCookie", "sDDDc8dIAgZSq67uJSXhtpQaHEi1XDOH")
//                    .setCharset("GBK")
                    .setCycleRetryTimes(2).setSleepTime(3000);
            String cookie = (String) redisTemplate.opsForValue().get(TOPHUBCOOKIEKEY);
            String defaultCookie = "itc_center_user=89a0wYOmOjVb3PG%2FaijYmYIVyorb2J4vRKak1zIB%2FKj%2B4sq%2BV1EMGWzHHzPWTeKd57noN5167iZBtsec1yUlBibKhAFYxdR4bZxGOGfSwPBMLS6bpQnTSYs";
            if (cookie == null) {
                log.info("tophub 网站 cookie未设定,使用默认，并写入redis");
                redisTemplate.opsForValue().set(TOPHUBCOOKIEKEY, defaultCookie);
                site.addHeader("cookie", defaultCookie);
            } else {
                site.addHeader("cookie", cookie);
            }

        }
        return site;
    }


//    public static void main(String[] args) {
//        HubTopPageProcessor pageProcessor = new HubTopPageProcessor();
//        Spider.create(pageProcessor).addUrl("https://tophub.today/").addPipeline(new ConsolePipeline())
////                .setScheduler(new RedisScheduler(getJedisPool()))
//                .setScheduler(new QueueScheduler().setDuplicateRemover(new BloomFilterDuplicateRemover(10000000))).thread(3).run();
////        Spider spider = Spider.create(pageProcessor)
////                .addUrl("http://127.0.0.1")
////                .setScheduler(new QueueScheduler().setDuplicateRemover(new BloomFilterDuplicateRemover(10000000)))
////                .thread(3);
////        spider.run();
//
//    }
}
