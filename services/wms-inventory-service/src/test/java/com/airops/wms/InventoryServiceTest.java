package com.airops.wms;

import com.airops.wms.kafka.InventoryEventProducer;
import com.airops.wms.model.InventoryItem;
import com.airops.wms.model.Location;
import com.airops.wms.repository.InventoryItemRepository;
import com.airops.wms.repository.LocationRepository;
import com.airops.wms.service.InventoryService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.StoredProcedureQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("InventoryService Unit Tests")
class InventoryServiceTest {

    @Mock private InventoryItemRepository inventoryRepo;
    @Mock private LocationRepository locationRepo;
    @Mock private EntityManager entityManager;
    @Mock private InventoryEventProducer eventProducer;
    @Mock private StoredProcedureQuery storedProcedureQuery;

    @InjectMocks
    private InventoryService inventoryService;

    private UUID testItemId;
    private UUID testLocationId;

    @BeforeEach
    void setUp() {
        testItemId = UUID.randomUUID();
        testLocationId = UUID.randomUUID();
    }

    @Nested
    @DisplayName("Receive Items")
    class ReceiveItemsTests {

        @Test
        @DisplayName("Should receive items successfully and publish Kafka event")
        void shouldReceiveItemsAndPublishEvent() {
            // Arrange
            when(entityManager.createStoredProcedureQuery("wms.receive_items"))
                    .thenReturn(storedProcedureQuery);
            when(storedProcedureQuery.registerStoredProcedureParameter(anyInt(), any(), any()))
                    .thenReturn(storedProcedureQuery);
            when(storedProcedureQuery.setParameter(anyInt(), any()))
                    .thenReturn(storedProcedureQuery);
            when(storedProcedureQuery.getSingleResult()).thenReturn(testItemId);

            // Act
            UUID result = inventoryService.receiveItems(
                    "SKU-001", "Airline Blanket", "CABIN_SUPPLIES",
                    100, testLocationId, "EMP001", "REF-2024-001"
            );

            // Assert
            assertThat(result).isEqualTo(testItemId);
            verify(storedProcedureQuery).execute();
            verify(eventProducer).publishInventoryReceived(testItemId, "SKU-001", 100, testLocationId);
        }

        @Test
        @DisplayName("Should throw exception when stored procedure fails")
        void shouldThrowWhenStoredProcedureFails() {
            when(entityManager.createStoredProcedureQuery("wms.receive_items"))
                    .thenThrow(new RuntimeException("DB connection failed"));

            assertThatThrownBy(() -> inventoryService.receiveItems(
                    "SKU-001", "Test", "CAT", 10, testLocationId, "EMP001", "REF-001"
            )).isInstanceOf(RuntimeException.class)
              .hasMessageContaining("DB connection failed");
        }
    }

    @Nested
    @DisplayName("Pick Items")
    class PickItemsTests {

        @Test
        @DisplayName("Should pick items successfully and publish Kafka event")
        void shouldPickItemsAndPublishEvent() {
            when(entityManager.createStoredProcedureQuery("wms.pick_items"))
                    .thenReturn(storedProcedureQuery);
            when(storedProcedureQuery.registerStoredProcedureParameter(anyInt(), any(), any()))
                    .thenReturn(storedProcedureQuery);
            when(storedProcedureQuery.setParameter(anyInt(), any()))
                    .thenReturn(storedProcedureQuery);
            when(storedProcedureQuery.getSingleResult()).thenReturn(Boolean.TRUE);

            boolean result = inventoryService.pickItems("SKU-001", 5, "EMP002", "SHIP-REF-001");

            assertThat(result).isTrue();
            verify(eventProducer).publishItemsPicked("SKU-001", 5, "SHIP-REF-001");
        }
    }

    @Nested
    @DisplayName("Query Operations")
    class QueryTests {

        @Test
        @DisplayName("Should find item by SKU")
        void shouldFindItemBySku() {
            InventoryItem item = InventoryItem.builder()
                    .id(testItemId).sku("SKU-001")
                    .description("Airline Blanket").category("CABIN_SUPPLIES")
                    .quantity(50).status(InventoryItem.ItemStatus.AVAILABLE)
                    .build();

            when(inventoryRepo.findBySku("SKU-001")).thenReturn(Optional.of(item));

            InventoryItem result = inventoryService.findBySku("SKU-001");

            assertThat(result.getSku()).isEqualTo("SKU-001");
            assertThat(result.getQuantity()).isEqualTo(50);
        }

        @Test
        @DisplayName("Should throw when SKU not found")
        void shouldThrowWhenSkuNotFound() {
            when(inventoryRepo.findBySku("UNKNOWN-SKU")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> inventoryService.findBySku("UNKNOWN-SKU"))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Item not found");
        }

        @Test
        @DisplayName("Should return low stock items below threshold")
        void shouldReturnLowStockItems() {
            InventoryItem lowItem = InventoryItem.builder()
                    .sku("SKU-LOW").quantity(3)
                    .status(InventoryItem.ItemStatus.AVAILABLE).build();

            when(inventoryRepo.findByQuantityLessThanAndStatus(10, InventoryItem.ItemStatus.AVAILABLE))
                    .thenReturn(List.of(lowItem));

            List<InventoryItem> result = inventoryService.findLowStockItems(10);

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getSku()).isEqualTo("SKU-LOW");
        }
    }
}
