package net.liuxuan.crawler.webmagic.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.scheduler.RedisScheduler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Liuxuan
 * @version v1.0.0
 * @description 自定义调度器, 添加部分额外特性
 * @date 2023/2/1
 **/

@Slf4j
public class SpiderRedisScheduler extends RedisScheduler {


    private static final String QUEUE_PREFIX = "queue_";

    private static final String SET_PREFIX = "set_";

    private static final String ITEM_PREFIX = "item_";

    private static final List<String> duplicateIgnoreList = new ArrayList<>();

    private static final Map<String, AtomicInteger> tempIgnoreMap = new ConcurrentHashMap<>();

    Task taskSave = null;
    String UUID = null;

    public SpiderRedisScheduler(JedisPool pool) {
        super(pool);
    }

    /**
     * @param request request
     * @param task    task
     * @return
     */
    @Override
    public boolean isDuplicate(Request request, Task task) {
        memoryTaskSave(task);
        if (tempIgnoreMap.containsKey(request.getUrl())) {
            AtomicInteger count = tempIgnoreMap.get(request.getUrl());
            int i = count.decrementAndGet();
            if (i < 1) {
                tempIgnoreMap.remove(request.getUrl());
                return false;
            }
        }
        //遍历duplicateIgnoreList
        if (duplicateIgnoreList.contains(request.getUrl())) {
            return false;
        }
        return super.isDuplicate(request, task);
    }

    /**
     * @param request
     * @param task
     */
    @Override
    protected void pushWhenNoDuplicate(Request request, Task task) {
        memoryTaskSave(task);
        super.pushWhenNoDuplicate(request, task);
    }

    /**
     * @param task the task of spider
     * @return
     */
    @Override
    public synchronized Request poll(Task task) {
        memoryTaskSave(task);
        return super.poll(task);
    }

    /**
     * @param request request
     * @param task    task
     */
    @Override
    public void push(Request request, Task task) {
        memoryTaskSave(task);
        super.push(request, task);
    }

    private void memoryTaskSave(Task t) {
        if (null == t) {
            return;
        }
        if (taskSave == null) {
            taskSave = t;
        } else {
            if (taskSave == t) {
                //一致
            } else {
                log.info("task切换了？ [{}]-->[{}]", taskSave, t);
            }
        }
    }

    /**
     * 从redis移除url
     *
     * @param url
     * @return
     */
    public boolean removeUrl(String url) {
        if (taskSave == null) {
            return false;
        }
        String uuid = taskSave.getUUID();
        return removeUrl(url, uuid);
    }

    /**
     * 从redis移除url
     *
     * @param url
     * @param uuid
     * @return
     */
    public boolean removeUrl(String url, String uuid) {
        String queueKey = QUEUE_PREFIX + uuid;
        String set_key = SET_PREFIX + uuid;
        String item_key = ITEM_PREFIX + uuid;
        Jedis jedis = pool.getResource();
        try {
            String field = DigestUtils.sha1Hex(url);
            Long del = jedis.del(queueKey);
            Long srem = jedis.srem(set_key, url);
            Long hdel1 = jedis.hdel(item_key, field);
            if ((del + srem + hdel1) > 0) {
                return true;
            }
        } finally {
            jedis.close();
        }
        return false;
    }

    public Task getTaskSave() {
        return taskSave;
    }

    /**
     * 把url添加到忽略重复的list
     *
     * @param url
     * @return
     */
    public boolean addUrlToDuplicateIgnoreList(String url) {
        return duplicateIgnoreList.add(url);
    }

    /**
     * 把url从忽略重复的urlList移除
     *
     * @param url
     * @return
     */
    public boolean removeUrlFromDuplicateIgnoreList(String url) {
        return duplicateIgnoreList.remove(url);
    }

    public boolean addUrlToTempIgnoreMap(String url, int times) {
        if (tempIgnoreMap.containsKey(url)) {
            AtomicInteger count = tempIgnoreMap.get(url);
            count.addAndGet(times);
        } else {
            tempIgnoreMap.put(url, new AtomicInteger(times));
        }
        return true;
    }

    public void removeUrlFromTempIgnoreMap(String url) {
        if (tempIgnoreMap.containsKey(url)) {
            tempIgnoreMap.remove(url);
        }
    }

}
