package com.ptit.clone.entity;

import lombok.*;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CollectionDocument {

    @Field(name = "collection_slug", type = FieldType.Keyword, normalizer = "keyword_lowercase")
    private String collectionSlug;

    @Field(name = "collection_name", type = FieldType.Text, analyzer = "product_text_index", searchAnalyzer = "product_text_search")
    private String collectionName;

    @Field(name = "hero_banner", type = FieldType.Keyword, index = false)
    private String heroBanner;
}
