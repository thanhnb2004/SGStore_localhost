package com.ptit.clone.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttributeDocument {
    @Field(type = FieldType.Keyword, normalizer = "keyword_lowercase")
    private String code;

    @Field(type = FieldType.Text, analyzer = "product_text_index", searchAnalyzer = "product_text_search")
    private String name;

    @Field(type = FieldType.Text, analyzer = "product_text_index", searchAnalyzer = "product_text_search")
    private String value;
}
