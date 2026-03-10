package com.airops.wms.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
@Slf4j
public class InventoryEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.topics.inventory-events}")
    private String inventoryEventsTopic;

    @Value("${kafka.topics.shipment-events}")
    private String shipmentEventsTopic;

    public void publishInventoryReceived(UUID itemId, String sku, int quantity, UUID locationId) {
        Map<String, Object> event = Map.of(
                "eventType", "INVENTORY_RECEIVED",
                "itemId", itemId.toString(),
                "sku", sku,
                "quantity", quantity,
                "locationId", locationId.toString(),
                "timestamp", Instant.now().toString()
        );
        sendEvent(inventoryEventsTopic, sku, event);
    }

    public void publishItemsPicked(String sku, int quantity, String referenceNo) {
        Map<String, Object> event = Map.of(
                "eventType", "ITEMS_PICKED",
                "sku", sku,
                "quantity", quantity,
                "referenceNo", referenceNo,
                "timestamp", Instant.now().toString()
        );
        sendEvent(inventoryEventsTopic, sku, event);
    }

    public void publishLowStockAlert(String sku, int currentQuantity, int threshold) {
        Map<String, Object> event = Map.of(
                "eventType", "LOW_STOCK_ALERT",
                "sku", sku,
                "currentQuantity", currentQuantity,
                "threshold", threshold,
                "timestamp", Instant.now().toString()
        );
        sendEvent(inventoryEventsTopic, sku, event);
        log.warn("LOW STOCK ALERT published for SKU: {} (qty={})", sku, currentQuantity);
    }

    private void sendEvent(String topic, String key, Map<String, Object> payload) {
        CompletableFuture<SendResult<String, Object>> future =
                kafkaTemplate.send(topic, key, payload);

        future.whenComplete((result, ex) -> {
            if (ex != null) {
                log.error("Failed to publish event to topic {}: {}", topic, ex.getMessage());
            } else {
                log.debug("Event published to topic={} partition={} offset={}",
                        topic,
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset());
            }
        });
    }
}
