package com.ptit.clone.entity;

import lombok.*;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductStatsDocument {
    @Field(name = "rating_average", type = FieldType.Double)
    private Double ratingAverage;

    @Field(name = "rating_count", type = FieldType.Integer)
    private Integer ratingCount;

    @Field(name = "sold_count", type = FieldType.Integer)
    private Integer soldCount;
}
