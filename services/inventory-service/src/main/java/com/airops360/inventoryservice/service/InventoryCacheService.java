package com.airops360.inventoryservice.service;

import com.airops360.inventoryservice.api.InventoryCacheRecord;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class InventoryCacheService {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;
    private final Duration ttl;

    public InventoryCacheService(
        StringRedisTemplate redisTemplate,
        ObjectMapper objectMapper,
        @Value("${inventory.cache.ttl:PT30M}") Duration ttl
    ) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
        this.ttl = ttl;
    }

    public InventoryCacheRecord cache(InventoryCacheRecord record) {
        try {
            String payload = objectMapper.writeValueAsString(record);
            redisTemplate.opsForValue().set(buildKey(record.sku()), payload, ttl);
            return record;
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Failed to serialize inventory cache record", exception);
        }
    }

    public Optional<InventoryCacheRecord> get(String sku) {
        String payload = redisTemplate.opsForValue().get(buildKey(sku));
        if (payload == null) {
            return Optional.empty();
        }

        try {
            return Optional.of(objectMapper.readValue(payload, InventoryCacheRecord.class));
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Failed to deserialize inventory cache record", exception);
        }
    }

    public InventoryCacheRecord fromRequest(
        String sku,
        String itemName,
        String storageZone,
        int quantityOnHand,
        int reorderThreshold
    ) {
        return new InventoryCacheRecord(
            sku,
            itemName,
            storageZone,
            quantityOnHand,
            reorderThreshold,
            OffsetDateTime.now()
        );
    }

    String buildKey(String sku) {
        return "inventory:sku:" + sku;
    }
}
