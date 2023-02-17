package net.liuxuan.crawler.spring.redis.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.lang.NonNull;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.time.Duration;
import java.util.HashSet;
import java.util.List;

/**
 * @author Liuxuan
 * @version v1.0.0
 * @description 注册redis的模板，否则只可以放string，string类型
 * @date 2019-03-20
 **/
@Configuration
@AutoConfigureAfter(RedisAutoConfiguration.class)
@Slf4j
public class RedisConfig {

    @Autowired
    RedisLettucePoolProperty redisLettucePoolProperty;

    @Value("${spring.redis.timeout}")
    private Integer timeout = 15000;


    @Bean(name = "springSessionDefaultRedisSerializer")
    @Primary
    public GenericJackson2JsonRedisSerializer springSessionDefaultRedisSerializer() {
        return new GenericJackson2JsonRedisSerializer();
    }

    @Autowired
    Redis113RedisProperty redis113RedisProperty;

    @Bean(name = "redis113RedisConnectionFactory")
    @ConditionalOnProperty(name = "spring.redis.redis113.enable", havingValue = "true", matchIfMissing = false)
    public RedisConnectionFactory redis113RedisConnectionFactory() {
        log.info("{}", redis113RedisProperty);
        return getConnectionFactory(redis113RedisProperty);
    }


    @Bean(name = "redis113StringRedisTemplate")
    @ConditionalOnProperty(name = "spring.redis.redis113.enable", havingValue = "true", matchIfMissing = false)
    public StringRedisTemplate redis113StringRedisTemplate(@Qualifier("redis113RedisConnectionFactory") RedisConnectionFactory cf) {
        StringRedisTemplate stringRedisTemplate = new StringRedisTemplate();
        stringRedisTemplate.setConnectionFactory(cf);
        return stringRedisTemplate;
    }

    @Bean(name = "redis113RedisTemplate")
    @ConditionalOnProperty(name = "spring.redis.redis113.enable", havingValue = "true", matchIfMissing = false)
    public RedisTemplate redis113RedisTemplate(@Qualifier("redis113RedisConnectionFactory") RedisConnectionFactory cf) {
        RedisTemplate<String, Object> template = new RedisTemplate<String, Object>();
        template.setConnectionFactory(cf);
        setSerializer2(template);
//        template.afterPropertiesSet();
        return template;
    }


    @Bean
    @Primary
    public StringRedisSerializer defaultRedisSerializer() {
        return new StringRedisSerializer();
    }

    public void setSerializer2(RedisTemplate<String, Object> template) {
        template.setDefaultSerializer(new GenericJackson2JsonRedisSerializer());
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
//        template.setHashKeySerializer(new GenericJackson2JsonRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
    }

    private void setSerializer(RedisTemplate<String, String> template) {
        template.setDefaultSerializer(new GenericJackson2JsonRedisSerializer());
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
//        template.setHashKeySerializer(new GenericJackson2JsonRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
//        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
    }

    public LettuceConnectionFactory getConnectionFactory(RedisProperty redisProperty) {
        //当cluster不为空时，使用clusters
        if (redisProperty.getClusters() != null && redisProperty.getClusters().size() > 0) {
            return getRedisClusterConnectionFactory(redisProperty);
        }
        //当sentinel不为空时，使用sentinel
        if (redisProperty.getSentinels() != null && redisProperty.getSentinels().size() > 0) {
            return getRedisSentinelConnectionFactory(redisProperty);
        }
        return getRedisStandardConnectionFactory(redisProperty);
    }

    private LettuceConnectionFactory getRedisStandardConnectionFactory(RedisProperty redisProperty) {
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
        redisStandaloneConfiguration.setHostName(redisProperty.getHost());
        redisStandaloneConfiguration.setDatabase(redisProperty.getDatabase());
        redisStandaloneConfiguration.setPassword(redisProperty.getPassword());
        redisStandaloneConfiguration.setPort(redisProperty.getPort());

        LettuceClientConfiguration clientConfig = getLettuceWithPoolClientConfiguration();
        LettuceConnectionFactory redisConnectionFactory = new LettuceConnectionFactory(redisStandaloneConfiguration, clientConfig);
        return redisConnectionFactory;
    }


    private LettuceConnectionFactory getRedisSentinelConnectionFactory(RedisProperty redisProperty) {

        RedisSentinelConfiguration redisSentinelConfiguration =
                new RedisSentinelConfiguration(redisProperty.getMasterName(), new HashSet<>(redisProperty.getSentinels()));
        redisSentinelConfiguration.setPassword(redisProperty.getPassword());
        redisSentinelConfiguration.setDatabase(redisProperty.getDatabase());
        // 配置池
//        LettuceClientConfiguration clientConfig = getLettuceWithPoolClientConfiguration();
//        LettuceConnectionFactory lettuceConnectionFactory = new LettuceConnectionFactory(redisSentinelConfiguration, clientConfig);
        LettuceConnectionFactory lettuceConnectionFactory = new LettuceConnectionFactory(redisSentinelConfiguration);
        log.debug("redis {} 哨兵启动", redisProperty.getMasterName());
        lettuceConnectionFactory.afterPropertiesSet();
        return lettuceConnectionFactory;

    }

    private LettuceConnectionFactory getRedisClusterConnectionFactory(RedisProperty redisProperty) {
        List<String> clusters = redisProperty.getClusters();
        if (clusters == null || clusters.size() == 0) {
            //应该抛出异常
            return null;
        }
        RedisClusterConfiguration redisClusterConfiguration = new RedisClusterConfiguration(clusters);
        redisClusterConfiguration.setMaxRedirects(5);
        redisClusterConfiguration.setPassword(redisProperty.getPassword());
//        for (String cluster : clusters) {
//            String[] split = cluster.split(":");
//            redisClusterConfiguration.addClusterNode(new RedisNode(split[0], Integer.parseInt(split[1])));
//        }
        if (redisClusterConfiguration.getClusterNodes().isEmpty()) {
            //应该抛出异常
            return null;
        }

        LettuceClientConfiguration clientConfig = getLettuceWithPoolClientConfiguration();
        LettuceConnectionFactory lettuceConnectionFactory = new LettuceConnectionFactory(redisClusterConfiguration, clientConfig);
        return lettuceConnectionFactory;

    }

    @NonNull
    private LettuceClientConfiguration getLettuceWithPoolClientConfiguration() {
        //连接池配置
        GenericObjectPoolConfig genericObjectPoolConfig =
                new GenericObjectPoolConfig();
        genericObjectPoolConfig.setMaxIdle(redisLettucePoolProperty.getMaxIdle());
        genericObjectPoolConfig.setMinIdle(redisLettucePoolProperty.getMinIdle());
        genericObjectPoolConfig.setMaxTotal(redisLettucePoolProperty.getMaxActive());
        genericObjectPoolConfig.setMaxWaitMillis(redisLettucePoolProperty.getMaxWait());

        LettuceClientConfiguration clientConfig = LettucePoolingClientConfiguration.builder()
                .commandTimeout(Duration.ofMillis(timeout))
                .poolConfig(genericObjectPoolConfig)
                .build();
        return clientConfig;
    }


    @Bean
    public JedisPool redisPoolFactory() {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxIdle(redisLettucePoolProperty.getMaxIdle());
        jedisPoolConfig.setMaxWaitMillis(redisLettucePoolProperty.getMaxWait());

        return new JedisPool(jedisPoolConfig, redis113RedisProperty.getHost(), redis113RedisProperty.getPort(), timeout, redis113RedisProperty.getPassword(), redis113RedisProperty.getDatabase());
    }

}
