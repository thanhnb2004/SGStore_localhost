package com.ptit.clone.messaging.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class KeycloakEmailVerifiedEvent {
    private String keycloakUserId;
    private String email;
    private Instant verifiedAt;
}
