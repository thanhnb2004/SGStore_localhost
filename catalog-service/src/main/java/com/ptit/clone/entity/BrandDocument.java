package com.ptit.clone.entity;

import lombok.*;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BrandDocument {

    @Field(name = "brand_slug", type = FieldType.Keyword, normalizer = "keyword_lowercase")
    private String brandSlug;

    @Field(name = "brand_name", type = FieldType.Text, analyzer = "product_text_index", searchAnalyzer = "product_text_search")
    private String brandName;

    @Field(name = "brand_logo", type = FieldType.Keyword, index = false)
    private String brandLogo;
}
