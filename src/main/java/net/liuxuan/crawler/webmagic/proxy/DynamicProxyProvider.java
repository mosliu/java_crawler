package net.liuxuan.crawler.webmagic.proxy;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.proxy.Proxy;
import us.codecraft.webmagic.proxy.ProxyProvider;

import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Liuxuan
 * @version v1.0.0
 * @description net.liuxuan.crawler.webmagic.proxy 包下xxxx工具
 * @date 2023/2/14
 **/
public class DynamicProxyProvider implements ProxyProvider {
    // 日志
    private Logger logger = LoggerFactory.getLogger(getClass());
    // 轮询起始位置
    private final AtomicInteger pointer = new AtomicInteger(-1);
    // url 一次获取一个ip
    private final String ipPoolProxyUrl;
    private final int ipPoolSize;
    // 代理池
    private List<Proxy> proxies;

    public DynamicProxyProvider(String ipPoolProxyUrl, int ipPoolSize) {
        this.ipPoolProxyUrl = ipPoolProxyUrl;
        this.ipPoolSize = ipPoolSize;
        //初始化 ip池,达到指定容量
        this.proxies = get(ipPoolProxyUrl);
        while (this.proxies.size() < ipPoolSize) {
            List<Proxy> rs = get(ipPoolProxyUrl);
            this.proxies.addAll(rs);
        }
    }

    /**
     * 反向求出哪个代理下载了页面
     *
     * @param proxy
     * @param page
     * @param task
     */
    @Override
    public void returnProxy(Proxy proxy, Page page, Task task) {
        // todo 暂时用不到
    }

    @Override
    public Proxy getProxy(Task task) {
        // todo 从redis获取ip和端口，然后在切换，采用轮循方式获取代理
        if (proxies.size() == 0) {
            logger.error("redis获取的ip数量为: 0!" + proxies.size());
            return null;
        }
        logger.info("redis获取的ip数量为: " + proxies.size());
        Proxy proxy = proxies.get(incrForLoop(proxies));
        // todo 验证 Ip 是否可用，如果不可用直接去代理方新获取一个来用，并补充道redis中，删除不可用的那个。
        Boolean isIpUsefull = checkIpUsefull(proxy.getHost(), proxy.getPort());
        if (!isIpUsefull) {
            // 删除不可用
            proxies.remove(proxy);
            // 获取新的可用ip，填充到list
            List<Proxy> rs = get(ipPoolProxyUrl);
            proxies.addAll(rs);
            // 重新获取Ip,这里是刚从ip池供应商获得的，没有必要去验证有效性了。
            proxy = rs.get(0);
        }
        return proxy;
    }

    // 轮循算法
    private int incrForLoop(List<Proxy> proxies) {
        int p = pointer.incrementAndGet();
        int size = proxies.size();
        if (p < size) {
            return p;
        }
        while (!pointer.compareAndSet(p, p % size)) {
            p = pointer.get();
        }
        return p % size;
    }

    //检查代理有效性
    private static boolean checkIpUsefull(String ip, Integer port) {
        URL url;
        try {
            url = new URL("http://www.baidu.com");
            InetSocketAddress addr = new InetSocketAddress(ip, port);
            java.net.Proxy proxy = new java.net.Proxy(java.net.Proxy.Type.HTTP, addr);
            InputStream in;
            try {
                URLConnection conn = url.openConnection(proxy);
                conn.setConnectTimeout(2000);
                conn.setReadTimeout(2000);
                in = conn.getInputStream();
            } catch (Exception e) {
                return false;
            }
            String s = IOUtils.toString(in);
            if (s.indexOf("baidu") > 0) {
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }
    // 获取代理方的ip

    /**
     * Get请求
     *
     * @param url URL地址
     * @return 返回结果
     */
    private List<Proxy> get(String url) {
        List<Proxy> result = new ArrayList<>();
        try {
            OkHttpClient okHttpClient = new OkHttpClient();
            Request request = new Request.Builder().url(url).build();
            Response response = okHttpClient.newCall(request).execute();
            if (response.code() != 200) {
                logger.error("从Ip供应商获取ip失败：{}", result);
            }
            JSONObject jsonObject = (JSONObject) JSONObject.parse(response.body().string());
            JSONArray jsonArray = (JSONArray) jsonObject.get("data");
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject jsonProxy = (JSONObject) jsonArray.get(i);
                Proxy proxyTmp = new Proxy(jsonProxy.get("ip").toString(), Integer.valueOf(jsonProxy.get("port").toString()));
                result.add(proxyTmp);
            }
            return result;
        } catch (Exception e) {
            logger.error("从Ip供应商获取ip请求异常:", e);
            return result;
        }
    }
}
