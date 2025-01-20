package com.hana4.ggumtle.repository;

import java.time.Duration;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class RedisRepository implements CacheRepository {

	private final RedisTemplate<String, Object> redisTemplate;

	@Autowired
	public RedisRepository(RedisTemplate<String, Object> redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	@Override
	public String getValue(String key) {
		Object data = redisTemplate.opsForValue().get(key);
		return data.toString();
	}

	@Override
	public void setValue(String key, String value, int minutes) {
		redisTemplate.opsForValue().set(key, value, Duration.ofMinutes(minutes));
	}

	@Override
	public void setValue(String key, String value) {
		redisTemplate.opsForValue().set(key, value);
	}

	@Override
	public void setValue(String key, String value, Duration duration) {
		redisTemplate.opsForValue().set(key, value, duration);
	}

	@Override
	public void setValue(byte[] key, byte[] value) {
		setValue(new String(key), new String(value));
	}

	@Override
	public void setValue(byte[] key, byte[] value, Duration duration) {
		setValue(new String(key), new String(value), duration);
	}

	@Override
	public String getValue(byte[] key) {
		return getValue(new String(key));
	}

	@Override
	public void remove(byte[] key) {
		remove(new String(key));
	}

	@Override
	public void remove(String key) {
		redisTemplate.delete(key);
	}

	@Override
	public Long setValueHashes(String key, String subKey, String value) {
		redisTemplate.opsForHash().put(key, subKey, value);
		return 1L;
	}

	@Override
	public Long setValueHashes(String key, String subKey, String value, Duration duration) {
		redisTemplate.opsForHash().put(key, subKey, value);
		redisTemplate.expire(key, duration);
		return 1L;
	}

	@Override
	public String getValueHashes(String key, String subKey) {
		Object hashValue = redisTemplate.opsForHash().get(key, subKey);
		return hashValue.toString();
	}

	@Override
	public Map<Object, Object> getAllValueHashes(String key) {
		return redisTemplate.opsForHash().entries(key);
	}

	@Override
	public Long removeHashes(String key, String subKey) {
		return redisTemplate.opsForHash().delete(key, subKey);
	}

	@Override
	public Long countHashes(String key) {
		return redisTemplate.opsForHash().size(key);
	}
}
