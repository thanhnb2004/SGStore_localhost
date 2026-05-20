package com.ptit.clone.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(indexName = "products")
public class ProductDocument {
    @Id
    @Field(name = "variant_id", type = FieldType.Keyword, normalizer = "keyword_lowercase")
    private String variantId;

    //Product
    @Field(name = "product_id", type = FieldType.Keyword, normalizer = "keyword_lowercase")
    private String productId;

    @Field(type = FieldType.Keyword, normalizer = "keyword_lowercase")
    private String productSlug;

    @Field(name = "product_name", type = FieldType.Text, analyzer = "product_text_index", searchAnalyzer = "product_text_search")
    private String productName;

    @Field(name = "short_description", type = FieldType.Text, analyzer = "product_text_index", searchAnalyzer = "product_text_search")
    private String shortDescription;

    @Field(name = "hero_image", type = FieldType.Keyword, index = false)
    private String productHeroImage;

    @Field(name = "published_at", type = FieldType.Date, format = DateFormat.date_hour_minute_second_fraction)
    private LocalDateTime publishedAt;

    @Field(name = "brand", type = FieldType.Object)
    private BrandDocument brand;

    @Field(name = "product_type", type = FieldType.Object)
    private ProductTypeDocument productType;

    @Field(name = "collections", type = FieldType.Object)
    private List<CollectionDocument> collections;

    @Field(type = FieldType.Nested)
    private List<AttributeDocument> attributes;

    @Field(name = "variant", type = FieldType.Object)
    private VariantDocument variant;

    @Field(name = "product_stats", type = FieldType.Object)
    private ProductStatsDocument productStats;
}
