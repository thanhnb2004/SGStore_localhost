package com.ptit.clone.entity;

import lombok.*;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductTypeDocument {

    @Field(name = "product_type_slug", type = FieldType.Keyword, normalizer = "keyword_lowercase")
    private String productTypeSlug;

    @Field(name = "product_type_name", type = FieldType.Text, analyzer = "product_text_index", searchAnalyzer = "product_text_search")
    private String productTypeName;
}
