package net.liuxuan.crawler.spring.redis.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

/**
 * The type Redis access service.
 *
 * @author Liuxuan
 * @version v1.0.0
 * @description Tools for xx use
 * @date 2019 -03-21
 */
@Service("redis113AccessService")
@ConditionalOnProperty(name = "spring.redis.redis113.enable", havingValue = "true", matchIfMissing = false)
public class Redis113AccessServiceImpl implements RedisAccessService {

    @Autowired
    @Qualifier("redis113RedisTemplate")
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    @Qualifier("redis113StringRedisTemplate")
    private StringRedisTemplate stringRedisTemplate;

    //    @Override
//    public String hGet(String key, String field) {
//        Object val = redisTemplate.opsForHash().get(key, field);
//        return val == null ? null : val.toString();
//    }
//
//    @Override
//    public void hSet(String key, String field, String value) {
//        redisTemplate.opsForHash().put(key, field, value);
//    }
//
//    @Override
//    public void hSetStr(String key, String field, String value) {
//        redisStringTemplate.opsForHash().put(key, field, value);
//    }
//    @Override
//    public void hDel(String key, String field) {
//        redisTemplate.opsForHash().delete(key, field);
//    }
//
//
//    @Override
//    public Map<String, String> hGetAll(String key) {
//        return redisTemplate.execute((RedisCallback<Map<String, String>>) con -> {
//            Map<byte[], byte[]> result = con.hGetAll(key.getBytes());
//            if (CollectionUtils.isEmpty(result)) {
//                return new HashMap<>(0);
//            }
//
//            Map<String, String> ans = new HashMap<>(result.size());
//            for (Map.Entry<byte[], byte[]> entry : result.entrySet()) {
//                ans.put(new String(entry.getKey()), new String(entry.getValue()));
//            }
//            return ans;
//        });
//    }
//
//    @Override
//    public Map<String, String> hGetBatch(String key, List<String> fields) {
//        List<String> result = redisTemplate.<String, String>opsForHash().multiGet(key, fields);
//        Map<String, String> ans = new HashMap<>(fields.size());
//        int index = 0;
//        for (String field : fields) {
//            if (result.get(index) == null) {
//                continue;
//            }
//            ans.put(field, result.get(index));
//        }
//        return ans;
//    }
//
//    @Override
//    public long hIncr(String key, String field, long value) {
//        return redisTemplate.opsForHash().increment(key, field, value);
//    }
    public RedisTemplate<String, Object> getRedisTemplate() {
        return redisTemplate;
    }

    public StringRedisTemplate getStringRedisTemplate() {
        return stringRedisTemplate;
    }

}
