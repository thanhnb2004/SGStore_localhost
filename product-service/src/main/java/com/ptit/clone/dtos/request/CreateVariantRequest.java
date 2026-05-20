package com.ptit.clone.dtos.request;

import com.ptit.clone.model.AvailabilityStatus;
import com.ptit.clone.model.VariantStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateVariantRequest{
    @NotBlank
    private String sku;
    private String barcode;

    @NotBlank
    private String name;

    @NotEmpty
    private Map<String, String> optionValues;

    @NotNull
    @Min(0)
    private Long listPrice;

    @NotNull
    @Min(0)
    private Long salePrice;
    private AvailabilityStatus availabilityStatus;
    private String heroImage;
    private List<String> images;
    private VariantStatus status;
}
