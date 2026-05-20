package com.ptit.clone.keycloak;

import java.time.Instant;

public record KeycloakEmailVerifiedEvent(
        String keycloakUserId,
        String email,
        Instant verifiedAt
) {
}
