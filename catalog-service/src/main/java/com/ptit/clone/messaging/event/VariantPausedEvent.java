package com.ptit.clone.messaging.event;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VariantPausedEvent {
    private UUID productId;
    private UUID variantId;
}
