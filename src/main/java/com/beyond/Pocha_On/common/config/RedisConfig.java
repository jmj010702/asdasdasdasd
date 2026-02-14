package com.beyond.Pocha_On.common.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Value("${spring.redis.host1}")
    public String host;

    @Value("${spring.redis.port1}")
    public int port;

    //장바구니
    @Value("${spring.redis.host2}")
    private String host2;

    @Value("${spring.redis.port2}")
    private int port2;


    @Value("${spring.redis.host3}")
    public String emailHost;

    @Value("${spring.redis.port3}")
    public int emailPort;

    // groupId redis 6382
    @Value("${spring.redis.host4}")
    private String groupHost;

    @Value("${spring.redis.port4}")
    private int groupPort;

    // pubsub
    @Value("${spring.redis.host5}")
    private String pubsubHost;

    @Value("${spring.redis.port5}")
    private int pubsubPort;

    //idempotency redis 6381
    @Value("${spring.redis.host6}")
    private String idempotencyHost;

    @Value("${spring.redis.port6}")
    private int idempotencyPort;


    @Bean
    @Qualifier("rtInventory")
    public RedisConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration();
        configuration.setHostName(host);
        configuration.setPort(port);
        configuration.setDatabase(0);

        return new LettuceConnectionFactory(configuration);
    }

    @Bean
    @Qualifier("rtInventory")
    public RedisTemplate<String, String> redisTemplate(@Qualifier("rtInventory") RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        redisTemplate.setConnectionFactory(redisConnectionFactory);

        return redisTemplate;
    }


    @Bean
    @Qualifier("groupId")
    public RedisConnectionFactory redisConnectionFaqwectory() {
        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration();
        configuration.setHostName(host);
        configuration.setPort(port);
        configuration.setDatabase(0);

        return new LettuceConnectionFactory(configuration);
    }

    @Bean
    @Qualifier("groupId")
    public RedisTemplate<String, String> redisTemplaqwete(@Qualifier("groupId") RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        redisTemplate.setConnectionFactory(redisConnectionFactory);

        return redisTemplate;
    }

    @Bean
    @Qualifier("emailVerify")
    public RedisConnectionFactory emailVerifyRedisConnectionFactory() {
        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration();
        configuration.setHostName(emailHost);
        configuration.setPort(emailPort);
        configuration.setDatabase(1);

        return new LettuceConnectionFactory(configuration);
    }

    @Bean
    @Qualifier("emailVerify")
    public RedisTemplate<String, String> emailVerifyRedisTemplate(
            @Qualifier("emailVerify") RedisConnectionFactory connectionFactory
    ) {
        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new StringRedisSerializer());
        return template;
    }

    @Bean
    @Qualifier("smsVerify")
    public RedisConnectionFactory smsRedisConnectionFactory() {
        RedisStandaloneConfiguration config =
                new RedisStandaloneConfiguration();
        config.setHostName(host);
        config.setPort(port);
        config.setDatabase(2); // 인증용 DB 분리

        return new LettuceConnectionFactory(config);
    }

    @Bean
    @Qualifier("smsVerify")
    public RedisTemplate<String,String> smsRedisTemplate(
            @Qualifier("smsVerify") RedisConnectionFactory factory
    ){
        RedisTemplate<String,String> template=new RedisTemplate<>();
        template.setConnectionFactory(factory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new StringRedisSerializer());
        return template;
    }
}

