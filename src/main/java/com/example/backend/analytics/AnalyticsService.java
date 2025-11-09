package com.example.backend.analytics;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnalyticsService {
    private final ObjectMapper objectMapper;          // auto-provided by Spring Boot
    private List<SaleRow> rows;                       // loaded once

    // lazy-load so tests and app start fast even if file is big
    private synchronized void ensureLoaded() {
        if (rows != null) return;
        try (InputStream in = new ClassPathResource("sales_dummy_data.json").getInputStream()) {
            rows = objectMapper.readValue(in, new TypeReference<List<SaleRow>>() {});
        } catch (Exception e) {
            throw new IllegalStateException("Failed to load sales_dummy_data.json", e);
        }
    }

    // ---------- Task 1 ----------
    // Group by brandName & categoryName, sum salesIncVatActual; order categories asc
    public Map<String,Object> task1() {
        ensureLoaded();

        var grouped = rows.stream().collect(Collectors.groupingBy(
                SaleRow::getCategoryName,
                Collectors.groupingBy(SaleRow::getBrandName,
                        Collectors.summingDouble(SaleRow::getSalesIncVatActual))
        ));

        var categories = new ArrayList<>(grouped.keySet());
        Collections.sort(categories); // asc by category

        // collect all brand names to build consistent series
        var allBrands = new TreeSet<String>();
        grouped.values().forEach(m -> allBrands.addAll(m.keySet()));

        var xAxis = Map.of("type","category", "data", categories);
        var yAxis = Map.of("type","value");

        List<Map<String,Object>> series = new ArrayList<>();
        for (String brand : allBrands) {
            List<Double> values = new ArrayList<>(categories.size());
            for (String cat : categories) {
                values.add(grouped.getOrDefault(cat, Map.of()).getOrDefault(brand, 0.0));
            }
            series.add(Map.of("type","bar","name",brand,"data",values));
        }

        return Map.of("xAxis", xAxis, "yAxis", yAxis, "series", series);
    }

    // ---------- Task 2 ----------
    // Pie: top 4 categories by SUM(salesIncVatActual); slice value = total volume
    // Alpha ~ percentage of volume within top4; emphasis alpha = base+0.2 (cap 1, min 0.2)
    public Map<String,Object> task2() {
        ensureLoaded();

        var byCatSales = rows.stream().collect(Collectors.groupingBy(
                SaleRow::getCategoryName, Collectors.summingDouble(SaleRow::getSalesIncVatActual)));
        var byCatVolume = rows.stream().collect(Collectors.groupingBy(
                SaleRow::getCategoryName, Collectors.summingDouble(SaleRow::getVolume)));

        var top4 = byCatSales.entrySet().stream()
                .sorted((a,b) -> Double.compare(b.getValue(), a.getValue()))
                .limit(4)
                .map(Map.Entry::getKey)
                .toList();

        double totalVolumeTop4 = top4.stream().mapToDouble(c -> byCatVolume.getOrDefault(c,0.0)).sum();

        List<Map<String,Object>> data = new ArrayList<>();
        for (String cat : top4) {
            double vol = byCatVolume.getOrDefault(cat, 0.0);
            double pct = totalVolumeTop4 > 0 ? vol / totalVolumeTop4 : 0.0;
            double baseAlpha = Math.max(0.2, Math.min(1.0, pct));
            double emphAlpha = Math.min(1.0, baseAlpha + 0.2);

            Map<String,Object> itemStyle = Map.of(
                    "normal", Map.of("color", String.format("rgba(60,185,226,%.3f)", baseAlpha)),
                    "emphasis", Map.of("color", String.format("rgba(60,185,226,%.3f)", emphAlpha))
            );

            data.add(Map.of("name", cat, "value", vol, "itemStyle", itemStyle));
        }

        return Map.of("series", Map.of("type","pie","radius","70%","data", data));
    }

    // ---------- Task 3 ----------
    // Treemap: bucket by salesIncVatActual into: 0–10, 10–100, 100+
    public Map<String,Object> task3() {
        ensureLoaded();

        double b0=0, b1=0, b2=0;
        for (var r : rows) {
            double v = r.getSalesIncVatActual();
            if (v < 10) b0 += v;
            else if (v < 100) b1 += v;
            else b2 += v;
        }
        var data = List.of(
                Map.of("name","0-10",   "value", b0),
                Map.of("name","10-100", "value", b1),
                Map.of("name","100+",   "value", b2)
        );
        return Map.of("series", List.of(Map.of("type","treemap","data", data)));
    }
}
