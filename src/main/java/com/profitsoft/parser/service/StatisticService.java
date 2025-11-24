package com.profitsoft.parser.service;

import com.profitsoft.parser.entity.Product;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StatisticService {

    public Map<String, Long> calculateStatistics(Stream<Product> productStream, String attributeName) {

        Stream<String> valueStream = productStream.flatMap(product -> {
            try {
                String rawValue = getAttributeValue(product, attributeName);

                if (rawValue == null || rawValue.trim().isEmpty()) {
                    return Stream.empty();
                }

                if ("tags".equalsIgnoreCase(attributeName)) {
                    return Arrays.stream(rawValue.split(","))
                            .map(String::trim)
                            .filter(s -> !s.isEmpty());
                }

                return Stream.of(rawValue.trim());

            } catch (Exception e) {
                System.err.println(attributeName + e.getMessage());
                return Stream.empty();
            }
        });

        Map<String, Long> rawStatistics = valueStream.collect(
                Collectors.groupingBy(Function.identity(), Collectors.counting())
        );

        return rawStatistics.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue,
                        LinkedHashMap::new
                ));
    }

    private String getAttributeValue(Product product, String attributeName) throws Exception {
        String methodName = "get" + Character.toUpperCase(attributeName.charAt(0)) + attributeName.substring(1);

        Method method = Product.class.getMethod(methodName);
        Object value = method.invoke(product);

        return value != null ? value.toString() : null;
    }
}