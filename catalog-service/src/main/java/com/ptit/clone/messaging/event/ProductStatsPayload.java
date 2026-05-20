package com.ptit.clone.messaging.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductStatsPayload {
    private Double ratingAverage; //Co the bi null
    private Integer ratingCount; //Co the bi null
    private Integer soldCount;
}
