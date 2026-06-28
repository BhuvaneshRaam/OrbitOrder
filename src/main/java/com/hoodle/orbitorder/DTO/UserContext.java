package com.hoodle.orbitorder.DTO;

import java.util.UUID;

public record UserContext(UUID userId, UUID tenantId) {
}
