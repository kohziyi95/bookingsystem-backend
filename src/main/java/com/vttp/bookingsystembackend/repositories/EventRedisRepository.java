package com.vttp.bookingsystembackend.repositories;

import java.util.Optional;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import com.vttp.bookingsystembackend.models.EventDetails;

import antlr.debug.Event;

@Repository
public class EventRedisRepository {
    private Logger logger = Logger.getLogger(EventRedisRepository.class.getName());

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private final String MAP_KEY = "event_map";

    public long count() {
        long count = redisTemplate.opsForHash().keys(MAP_KEY).size();
        logger.info("REDIS >>>> Number of entries: %s".formatted(count));
        return count;
    }

    public void deleteById(Integer eventId) {
        redisTemplate.opsForHash().delete(MAP_KEY, eventId.toString());
    }

    // public void deleteAll() {
    //     redisTemplate.opsForHash().delete(MAP_KEY, findAll());
    // }

    public boolean existsById(Integer eventId) {
        return redisTemplate.opsForHash().hasKey(MAP_KEY, eventId.toString());
    }

    public Iterable<EventDetails> findAll() {
        return redisTemplate.opsForHash().entries(MAP_KEY).values().stream().map(eventDetails -> (EventDetails) eventDetails).toList();
    }

    public Optional<EventDetails> findById(Integer eventId) {
        EventDetails eventDetails = (EventDetails) redisTemplate.opsForHash().get(MAP_KEY, eventId.toString());
        return Optional.of(eventDetails);
    }

    public <S extends EventDetails> S save(S entity) {
        redisTemplate.opsForHash().putIfAbsent(MAP_KEY, entity.getId().toString(), entity);
        return entity;
    }
}
