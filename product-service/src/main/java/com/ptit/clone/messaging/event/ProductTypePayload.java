package com.ptit.clone.messaging.event;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductTypePayload {
    private String id;
    private String slug;
    private String name;
}
