package com.ptit.clone.dtos.request;

import com.ptit.clone.model.AvailabilityStatus;
import com.ptit.clone.model.VariantStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateVariantRequest {
    private String barcode;
    private String name;
    private Map<String, String> optionValues;
    private Long listPrice;
    private Long salePrice;
    private AvailabilityStatus availabilityStatus;
    private String heroImage;
    private List<String> images;
    private VariantStatus status;
}
