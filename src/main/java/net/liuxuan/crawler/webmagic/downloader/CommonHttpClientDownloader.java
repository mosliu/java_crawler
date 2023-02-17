package net.liuxuan.crawler.webmagic.downloader;

import us.codecraft.webmagic.downloader.HttpClientDownloader;
import us.codecraft.webmagic.proxy.ProxyProvider;

/**
 * @author Liuxuan
 * @version v1.0.0
 * @description net.liuxuan.crawler.webmagic.downloader 包下xxxx工具
 * @date 2023/2/13
 **/
public class CommonHttpClientDownloader extends HttpClientDownloader {
    @Override
    public void setProxyProvider(ProxyProvider proxyProvider) {
        super.setProxyProvider(proxyProvider);
    }
}
