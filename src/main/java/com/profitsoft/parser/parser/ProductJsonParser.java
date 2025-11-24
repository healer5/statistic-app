package com.profitsoft.parser.parser;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.profitsoft.parser.entity.Product;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class ProductJsonParser {

    private final ObjectMapper mapper = new ObjectMapper();
    private final JsonFactory jsonFactory = new JsonFactory();

    public Stream<Product> parseFile(Path filePath) throws IOException {
        JsonParser jsonParser = jsonFactory.createParser(filePath.toFile());

        if (jsonParser.nextToken() != JsonToken.START_ARRAY) {
            throw new IOException("Очікувався початок масиву продуктів у файлі: " + filePath);
        }

        Spliterator<Product> spliterator = new Spliterators.AbstractSpliterator<>(
                Long.MAX_VALUE, Spliterator.ORDERED | Spliterator.NONNULL) {

            @Override
            public boolean tryAdvance(java.util.function.Consumer<? super Product> action) {
                try {
                    if (jsonParser.nextToken() == JsonToken.START_OBJECT) {
                        Product product = mapper.readValue(jsonParser, Product.class);
                        action.accept(product);
                        return true;
                    } else if (jsonParser.currentToken() == JsonToken.END_ARRAY) {
                        jsonParser.close();
                        return false;
                    } else {
                        jsonParser.close();
                        return false;
                    }
                } catch (IOException e) {
                    throw new RuntimeException();
                }
            }
        };

        return StreamSupport.stream(spliterator, false)
                .onClose(() -> {
                    try {
                        jsonParser.close();
                    } catch (IOException e) {
                    }
                });
    }
}