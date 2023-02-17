package net.liuxuan.crawler.webmagic.processor;

import lombok.extern.slf4j.Slf4j;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;

/**
 * @author Liuxuan
 * @version v1.0.0
 * @description net.liuxuan.crawler hubtop.today爬取
 * @date 2023/1/28
 **/
@Slf4j
public class WeChatPageProcessor implements PageProcessor {
    // 部分一：抓取网站的相关配置，包括编码、抓取间隔、重试次数等
    private Site site;

    public static final String weixinUrlRegex = "https://mp\\.weixin\\.qq\\.com/s*";

    // process是定制爬虫逻辑的核心接口，在这里编写抽取逻辑
    @Override
    public void process(Page page) {

        log.info("process url:{}", page.getUrl());

        //判断是否为列表的url
        if (page.getUrl().regex(weixinUrlRegex).match()) {
            //  //*[@id="activity-name"]
            String title = page.getHtml().css("#activity-name", "text").get();
            page.putField("title", title);
            String copyright_logo = page.getHtml().css("#copyright_logo", "text").get();
            page.putField("copyright_logo", copyright_logo);
            String rich_media_meta_text = page.getHtml().css("#copyright_logo", "text").get();
            page.putField("rich_media_meta_text", rich_media_meta_text);
            String rich_media_meta_nickname = page.getHtml().css(".rich_media_meta_nickname a", "text").get();
            page.putField("rich_media_meta_nickname", rich_media_meta_nickname);
            //微信号
            String profile_meta_value = page.getHtml().css(".profile_meta_value", "text").nodes().get(0).get();
            page.putField("profile_meta_value", profile_meta_value);
            //功能介绍
            String profile_meta_value1 = page.getHtml().css(".profile_meta_value", "text").nodes().get(1).get();
            page.putField("profile_meta_value1", profile_meta_value1);
            String time =  page.getHtml().css("#publish_time", "text").get();
            page.putField("time", time);
            String location =  page.getHtml().css("#js_ip_wording", "text").get();
            page.putField("location", location);
            String content = page.getHtml().css(".rich_media_content").smartContent().toString();
            page.putField("content", content);

        }
    }

    @Override
    public Site getSite() {
        if (site == null) {
            site = Site.me().setDomain("mp.weixin.qq.com")
                    .setUserAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_2) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.65 Safari/537.31")
//                    .addCookie("RegisteredUserCookie", "sDDDc8dIAgZSq67uJSXhtpQaHEi1XDOH")
//                    .setCharset("GBK")
                    .setCycleRetryTimes(2)
                    .setSleepTime(5000);
        }
        return site;
    }


}
