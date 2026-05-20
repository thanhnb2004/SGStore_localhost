package com.ptit.clone.entity;

import lombok.*;
import org.springframework.data.elasticsearch.annotations.Dynamic;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VariantDocument {

    @Field(name = "sku", type = FieldType.Keyword, normalizer = "keyword_lowercase")
    private String sku;

    @Field(name = "variant_name", type = FieldType.Text, analyzer = "product_text_index", searchAnalyzer = "product_text_search")
    private String variantName;


    @Field(name = "list_price", type = FieldType.Long)
    private Long listPrice;

    @Field(name = "sale_price", type = FieldType.Long)
    private Long salePrice;

    @Field(name = "availability_status", type = FieldType.Keyword)
    private String availabilityStatus;

    @Field(name = "variant_hero_image", type = FieldType.Keyword, index = false)
    private String variantHeroImage;

    @Field(name = "option_values", type = FieldType.Object, dynamic = Dynamic.TRUE)
    private Map<String, String> optionValues;
}
