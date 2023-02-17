package net.liuxuan.crawler.spring.redis.service;

import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The interface Redis access service.
 *
 * @author Liuxuan
 * @version v1.0.0
 * @description Tools for xx use
 * @date 2019 -03-21
 */
public interface RedisAccessService {
    default void setObj(String key, Object value) {
        getRedisTemplate().opsForValue().set(key, value);
    }

    default Object getObj(String key) {
        return getRedisTemplate().opsForValue().get(key);
    }

    public RedisTemplate<String, Object> getRedisTemplate();

    public StringRedisTemplate getStringRedisTemplate();

    /**
     * 获取key->value,String
     */
    default String getValueStr(String key) {
        return getStringRedisTemplate().opsForValue().get(key);
    }

    /**
     * 获取key->value,Object
     */
    default Object getValue(String key) {
        return getRedisTemplate().opsForValue().get(key);
    }

    /**
     * 删除 key
     */
    default Object delete(String key) {
        return getRedisTemplate().delete(key);
    }

    /**
     * 删除 key
     */
    default Object delete(Collection<String> keys) {
        return getRedisTemplate().delete(keys);
    }

    /**
     * 删除hash中的keys
     */
    default Object deleteHKey(String key, Object... hashKeys) {
        return getRedisTemplate().opsForHash().delete(key, hashKeys);
    }

    /**
     * 设定 key->value,String
     *
     * @param key   String key
     * @param value String value
     */
    default void setValueStr(String key, String value) {
        getStringRedisTemplate().opsForValue().set(key, value);
    }

    /**
     * 设定 key->value,Object
     *
     * @param key   String key
     * @param value Object value
     */
    default void setValue(String key, Object value) {
        getRedisTemplate().opsForValue().set(key, value);
    }

    /**
     * 获取hash中field对应的值
     *
     * @param key   the key
     * @param field the field
     * @return the string
     */
    default Object hGet(String key, String field) {
        Object val = getRedisTemplate().opsForHash().get(key, field);
        return val;
    }

    /**
     * 添加or更新hash的值
     *
     * @param key   the key
     * @param field the field
     * @param value the value
     */
    default void hSet(String key, String field, Object value) {
        getRedisTemplate().opsForHash().put(key, field, value);
    }

    default void hSetStr(String key, String field, String value) {
        getStringRedisTemplate().opsForHash().put(key, field, value);
    }

    default String hGetStr(String key, String field) {
        return getStringRedisTemplate().<String, String>opsForHash().get(key, field);
    }

    /**
     * 删除hash中field这一对kv
     *
     * @param key   the key
     * @param field the field
     */
    default void hDel(String key, String field) {
        getRedisTemplate().opsForHash().delete(key, field);
    }

    /**
     * 全量获取
     *
     * @param key the key
     * @return map map
     */
    default Map<String, String> hGetAll(String key) {
        return getRedisTemplate().execute((RedisCallback<Map<String, String>>) con -> {
            Map<byte[], byte[]> result = con.hGetAll(key.getBytes());
            if (CollectionUtils.isEmpty(result)) {
                return new HashMap<>(0);
            }

            Map<String, String> ans = new HashMap<>(result.size());
            for (Map.Entry<byte[], byte[]> entry : result.entrySet()) {
                ans.put(new String(entry.getKey()), new String(entry.getValue()));
            }
            return ans;
        });
    }

    /**
     * 批量获取
     *
     * @param key    the key
     * @param fields the fields
     * @return the map
     */
    default Map<String, String> hGetBatch(String key, List<String> fields) {
        List<String> result = getRedisTemplate().<String, String>opsForHash().multiGet(key, fields);
        Map<String, String> ans = new HashMap<>(fields.size());
        int index = 0;
        for (String field : fields) {
            if (result.get(index) == null) {
                continue;
            }
            ans.put(field, result.get(index));
        }
        return ans;
    }


    /**
     * 自增
     *
     * @param key   the key
     * @param field the field
     * @param value the value
     * @return the long
     */
    default long hIncr(String key, String field, long value) {
        return getRedisTemplate().opsForHash().increment(key, field, value);
    }

    /**
     * 判断变量中是否有指定的map键。
     *
     * @param key
     * @param field
     * @return
     */
    default boolean hHasKey(String key, String field) {
        return getRedisTemplate().opsForHash().hasKey(key, field);
    }

}
