package net.liuxuan.crawler.webmagic.domain;

import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import net.liuxuan.crawler.utils.JacksonUtils;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * @author Liuxuan
 * @version v1.0.0
 * @description site域，site初始化信息。
 * @date 2023/2/7
 **/
@Data
@Accessors(chain = true)
@Slf4j
public class SiteDomain {
    /**
     * 间隔时间，毫秒
     */
    private int sleepTime = 500;
    /**
     * 超时时间，毫秒
     */
    private int timeout = 3000;
    private int retryTimes = 10;
    /**
     * 重试间隔时间，毫秒
     */
    private int retrySleepTime = 3000;
    private String charset = "utf-8";
    private String domain = null;
    private String userAgent = "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.131 Safari/537.36";
    private Map<String, String> headers = null;
    private int cycleRetryTimes = 10;

    /**
     * 修正错误
     */
    public void correctError() {
        if (sleepTime < 0) sleepTime = 500;
        if (timeout < 0) timeout = 3000;
        if (retryTimes < 0) retryTimes = 10;
        if (retrySleepTime < 0) retrySleepTime = 3000;
    }

    public static SiteDomain fromJson(String siteConf) {
        if (isBlank(siteConf)) {
            return null;
        }
        SiteDomain siteDomain;
        try {
            siteDomain = JacksonUtils.str2Object(siteConf, SiteDomain.class);
        } catch (Exception e) {
            siteDomain = null;
        }
        if (siteDomain != null) {
            siteDomain.correctError();
        }
        return siteDomain;
    }

    public static void main(String[] args) {
        SiteDomain siteDomain = new SiteDomain();
        siteDomain.setDomain("www.douyin.com").setCharset("UTF-8")
                .setUserAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_2) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.65 Safari/537.31")
                .setRetryTimes(5).setRetrySleepTime(3000).setTimeout(10000).setSleepTime(5000);
        Map<String, String> headers = new LinkedHashMap<>();
        headers.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        headers.put("Accept-Encoding", "gzip,deflate,sdch");
        headers.put("Accept-Language", "zh-CN,zh;q=0.8");
        headers.put("Cache-Control", "max-age=0");
        headers.put("Connection", "keep-alive");
        headers.put("Host", "www.douyin.com");
        headers.put("Upgrade-Insecure-Requests", "1");
        headers.put("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_2) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.65 Safari/537.31");
        siteDomain.setHeaders(headers);
        String s = JacksonUtils.obj2String(siteDomain);
        SiteDomain siteDomain1 = JacksonUtils.str2Object(s, SiteDomain.class);

        log.info("{}", s);
        log.info("{}", siteDomain1);


        String topHubUrlRegex = "https://tophub\\.today/*";
        String url = "https://tophub.today/n/x9ozqX7eXb";
        log.info("{}", url.matches(topHubUrlRegex));

    }

}
