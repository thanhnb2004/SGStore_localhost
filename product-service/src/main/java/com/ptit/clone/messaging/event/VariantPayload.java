package com.ptit.clone.messaging.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VariantPayload {
    private String id;
    private String sku;
    private String barcode;
    private String name;
    private String status;
    private Long listPrice;
    private Long salePrice;
    private String availabilityStatus;
    private String heroImage;
    private List<String> images;
    private Map<String, String> optionValues;
}
