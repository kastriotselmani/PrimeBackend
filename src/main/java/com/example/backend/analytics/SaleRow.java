package com.example.backend.analytics;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SaleRow {
    private String brandName;
    private String categoryName;
    private double salesIncVatActual;
    private double volume;
}
