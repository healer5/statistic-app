package com.profitsoft.parser.cli;

import com.profitsoft.parser.entity.Product;
import com.profitsoft.parser.parser.ProductJsonParser;
import com.profitsoft.parser.service.StatisticService;
import com.profitsoft.parser.writer.XmlStatisticWriter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Application {

    private static final int DEFAULT_THREAD_COUNT = 4;

    public static void main(String[] args) {
        if (args.length < 2) {
            return;
        }

        Path inputFolder = Paths.get(args[0]);
        String attributeName = args[1];
        int threadCount = args.length > 2 ? Integer.parseInt(args[2]) : DEFAULT_THREAD_COUNT;

        if (!Files.isDirectory(inputFolder)) {
            return;
        }

        System.out.printf(String.valueOf(inputFolder.toAbsolutePath()), attributeName, threadCount);

        long startTime = System.currentTimeMillis();

        try {
            processFiles(inputFolder, attributeName, threadCount);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

        long endTime = System.currentTimeMillis();
        System.out.printf(String.valueOf((endTime - startTime) / 1000.0));
    }

    private static void processFiles(Path inputFolder, String attributeName, int threadCount) throws IOException {

        List<Path> jsonFiles;
        try (Stream<Path> files = Files.list(inputFolder)) {
            jsonFiles = files.filter(path -> path.toString().toLowerCase().endsWith(".json"))
                    .collect(Collectors.toList());
        }

        if (jsonFiles.isEmpty()) {
            return;
        }

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        List<Future<Stream<Product>>> futures = new CopyOnWriteArrayList<>();

        ProductJsonParser parser = new ProductJsonParser();
        for (Path file : jsonFiles) {
            Callable<Stream<Product>> task = () -> {
                System.out.printf(String.valueOf(Thread.currentThread().getId()), file.getFileName());
                return parser.parseFile(file);
            };
            futures.add(executor.submit(task));
        }

        Stream<Product> aggregatedProductStream = futures.stream()
                .flatMap(future -> {
                    try {
                        return future.get();
                    } catch (InterruptedException | ExecutionException e) {
                        System.err.println(e.getMessage());
                        return Stream.empty();
                    }
                });

        StatisticService statisticService = new StatisticService();
        Map<String, Long> statistics = statisticService.calculateStatistics(aggregatedProductStream, attributeName);

        XmlStatisticWriter writer = new XmlStatisticWriter();
        writer.write(statistics, attributeName, inputFolder);

        executor.shutdown();
    }
}