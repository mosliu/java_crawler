package net.liuxuan.crawler.spring.redis.service;

import net.liuxuan.crawler.spring.redis.config.Redis113RedisProperty;
import net.liuxuan.crawler.spring.redis.config.RedisConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

/**
 * @author Liuxuan
 * @version v1.0.0
 * @description com.sddzinfo.dzyq.redis.service 包下xxxx工具
 * @date 2022/12/15
 **/
@Service
public class RedisTemplateService {

    @Autowired
    Redis113RedisProperty redis113RedisProperty;

    @Autowired
    RedisConfig redisConfig = new RedisConfig();

    public RedisTemplate createNew113RedisTemplate(int db) {
        RedisTemplate<String, Object> template = new RedisTemplate<String, Object>();
        LettuceConnectionFactory cf = redisConfig.getConnectionFactory(redis113RedisProperty);
        cf.setDatabase(db);
        cf.afterPropertiesSet();
        template.setConnectionFactory(cf);
        redisConfig.setSerializer2(template);
        template.afterPropertiesSet();
        return template;
    }

    public StringRedisTemplate createNew113StringRedisTemplate(int db) {

        StringRedisTemplate stringRedisTemplate = new StringRedisTemplate();

        LettuceConnectionFactory cf = redisConfig.getConnectionFactory(redis113RedisProperty);
        cf.setDatabase(db);
        cf.afterPropertiesSet();
        stringRedisTemplate.setConnectionFactory(cf);
//        redisConfig.setSerializer2(stringRedisTemplate);
        stringRedisTemplate.afterPropertiesSet();
        return stringRedisTemplate;
    }

}
