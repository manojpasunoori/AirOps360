package com.airops360.inventoryservice.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.airops360.inventoryservice.api.InventoryCacheRecord;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

@ExtendWith(MockitoExtension.class)
class InventoryCacheServiceTests {

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @Test
    void cacheWritesSerializedPayloadToRedis() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        InventoryCacheService service = new InventoryCacheService(redisTemplate, objectMapper, Duration.ofMinutes(30));
        InventoryCacheRecord record = new InventoryCacheRecord(
            "SKU-100",
            "Cargo Net",
            "ZONE-A",
            25,
            5,
            OffsetDateTime.parse("2026-03-11T10:15:30Z")
        );

        service.cache(record);

        verify(valueOperations).set(eq("inventory:sku:SKU-100"), any(String.class), eq(Duration.ofMinutes(30)));
    }

    @Test
    void getReadsAndDeserializesCachedPayload() throws Exception {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        InventoryCacheService service = new InventoryCacheService(redisTemplate, objectMapper, Duration.ofMinutes(30));
        InventoryCacheRecord record = new InventoryCacheRecord(
            "SKU-100",
            "Cargo Net",
            "ZONE-A",
            25,
            5,
            OffsetDateTime.parse("2026-03-11T10:15:30Z")
        );
        when(valueOperations.get("inventory:sku:SKU-100")).thenReturn(objectMapper.writeValueAsString(record));

        Optional<InventoryCacheRecord> cachedRecord = service.get("SKU-100");

        assertThat(cachedRecord).isPresent();
        assertThat(cachedRecord.get().itemName()).isEqualTo("Cargo Net");
        assertThat(cachedRecord.get().storageZone()).isEqualTo("ZONE-A");
    }
}
