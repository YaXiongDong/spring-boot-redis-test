package com.pxkj.config;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * redis standalone config
 * 
 * @author Administrator
 * @param <T>
 *
 */
@Configuration
@EnableRedisRepositories
@EnableCaching
public class RedisConfig extends CachingConfigurerSupport {
	protected Log log = LogFactory.getLog(this.getClass());
	@Value("${spring.redis.host}")
	private String host;

	@Value("${spring.redis.port}")
	private int port;

	@Value("${spring.redis.timeout}")
	private int timeout;

	@Value("${spring.redis.password}")
	private String password;

	@Value("${spring.redis.database}")
	private int database;

	/**
	 * Redis集群节点地址
	 */
//	@Value("${spring.redis.cluster.nodes}")
//	private String clusterNodes;

	/**
	 * 使用jedis连接池的配置
	 * 
	 * @return
	 */
	@Bean(name = "redisConnectionFactory")
	public RedisConnectionFactory redisConnectionFactory() {
		RedisStandaloneConfiguration standaloneConfig = new RedisStandaloneConfiguration();
		standaloneConfig.setHostName(host);
		standaloneConfig.setDatabase(database);
		standaloneConfig.setPort(port);
		standaloneConfig.setPassword(RedisPassword.of(password));
		JedisClientConfiguration clientConfig = JedisClientConfiguration.defaultConfiguration();
		JedisConnectionFactory factory = new JedisConnectionFactory(standaloneConfig, clientConfig);
		return factory;
	}

	/**
	 * 使用Redis集群RedisConnectionFactory配置
	 */
//	@Bean(name = "redisConnectionFactory")
//	public RedisConnectionFactory redisConnectionFactory() {
//		RedisClusterConfiguration clusterConfiguration = new RedisClusterConfiguration();
//		String[] nodes = clusterNodes.split(",");
//		Set<RedisNode> set = new HashSet<>();
//		for (String node : nodes) {
//			String[] s = node.split(":");
//			RedisNode redisNode = new RedisNode(s[0], Integer.parseInt(s[1]));
//			set.add(redisNode);
//		}
//		clusterConfiguration.setClusterNodes(set);
//		JedisClientConfiguration clientConfig = JedisClientConfiguration.defaultConfiguration();
//		JedisConnectionFactory factory = new JedisConnectionFactory(clusterConfiguration, clientConfig);
//		return factory;
//	}

	/**
	 * 使用Redis缓存的序列化配置，使用Jackson序列化
	 * 
	 * @param redisConnectionFactory
	 * @return
	 */
	@Bean(name = "cacheManager")
	public RedisCacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
		RedisCacheWriter writer = RedisCacheWriter.nonLockingRedisCacheWriter(redisConnectionFactory);
		Jackson2JsonRedisSerializer<Object> serializer = new Jackson2JsonRedisSerializer<>(Object.class);
		RedisSerializationContext.SerializationPair<Object> pair = RedisSerializationContext.SerializationPair
				.fromSerializer(serializer);
		RedisCacheConfiguration configuration = RedisCacheConfiguration.defaultCacheConfig().serializeValuesWith(pair);
		return new RedisCacheManager(writer, configuration);
	}

	/**
	 * 使用spring session Redis 时的序列化配置，使用Jackson
	 * 
	 * @return
	 */
	@Bean(name = "springSessionDefaultRedisSerializer")
	public RedisSerializer<Object> defaultRedisSerializer() {
		return new GenericJackson2JsonRedisSerializer();
	}

	/**
	 * RedisTemplate配置，使用Jackson序列化方式
	 * 
	 * @param redisConnectionFactory
	 * @return
	 */
	@Bean(name = "redisTemplate")
	public <T> RedisTemplate<String, T> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
		RedisTemplate<String, T> redisTemplate = new RedisTemplate<String, T>();
		Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(
				Object.class);
		ObjectMapper om = new ObjectMapper();
		om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		om.setSerializationInclusion(Include.NON_NULL);
		om.setSerializationInclusion(Include.NON_EMPTY);
		jackson2JsonRedisSerializer.setObjectMapper(om);
		redisTemplate.setConnectionFactory(redisConnectionFactory);
		redisTemplate.setKeySerializer(new StringRedisSerializer());
		redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);
		redisTemplate.afterPropertiesSet();
		return redisTemplate;
	}
}
