package com.ptit.clone.dtos.response;

import com.ptit.clone.model.AvailabilityStatus;
import lombok.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
//Biến thể sản phẩm
public class ProductVariantResponse {
    private UUID variantId;
    private String sku; //Mã quản lý nội bộ
    private String barcode; // Mã vạch sản phẩm
    private String name; // Tên hiển thị biến thể sản phẩm
    private Map<String, String> optionValues; // Lưu thuộc tính biến thể (ví dụ: color:black)
    private PriceViewResponse price; //giá sản phẩm (giá gốc, khuyến mãi,..)
    private AvailabilityViewResponse availability;
    private String heroImage;      //Ảnh chính của sản phẩm
}
