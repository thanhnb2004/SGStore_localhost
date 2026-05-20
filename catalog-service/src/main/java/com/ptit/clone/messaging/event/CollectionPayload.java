package com.ptit.clone.messaging.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CollectionPayload {
    private String id;
    private String slug;
    private String name;
    private String heroBanner;
}
