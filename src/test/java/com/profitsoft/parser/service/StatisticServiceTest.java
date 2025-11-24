package com.profitsoft.parser.service;

import com.profitsoft.parser.entity.Product;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import static org.junit.jupiter.api.Assertions.*;

class StatisticServiceTest {

    private final StatisticService service = new StatisticService();

    private final List<Product> testProducts = List.of(
            new Product(1, "A", "ManuX", "T1, T2"),
            new Product(2, "B", "ManuY", "T1, T3"),
            new Product(3, "C", "ManuX", "T2"),
            new Product(4, "D", "ManuZ", "T1, T2, T3")
    );

    @Test
    void testCalculateStatistics_SimpleAttribute_Manufacturer() {
        Stream<Product> stream = testProducts.stream();
        String attribute = "manufacturer";

        Map<String, Long> result = service.calculateStatistics(stream, attribute);

        assertEquals(3, result.size());
        assertEquals(2L, result.get("ManuX"));
        assertEquals(1L, result.get("ManuY"));
        assertEquals(1L, result.get("ManuZ"));

        assertEquals("ManuX", result.keySet().stream().findFirst().orElseThrow());
    }

    @Test
    void testCalculateStatistics_MultiValueAttribute_Tags() {
        Stream<Product> stream = testProducts.stream();
        String attribute = "tags";

        Map<String, Long> result = service.calculateStatistics(stream, attribute);

        assertEquals(3, result.size());
        assertEquals(3L, result.get("T1"));
        assertEquals(3L, result.get("T2"));
        assertEquals(2L, result.get("T3"));

        assertTrue(List.of("T1", "T2").contains(result.keySet().stream().findFirst().orElseThrow()));

        List<Long> counts = result.values().stream().toList();
        assertTrue(counts.get(0) >= counts.get(1) && counts.get(1) >= counts.get(2));
    }

    @Test
    void testCalculateStatistics_AttributeNotFound_ThrowsException() {
        Stream<Product> stream = testProducts.stream();
        String attribute = "nonExistentAttribute";

        Map<String, Long> result = service.calculateStatistics(stream, attribute);

        assertTrue(result.isEmpty());
    }
}