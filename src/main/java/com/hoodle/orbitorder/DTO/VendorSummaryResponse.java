package com.hoodle.orbitorder.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VendorSummaryResponse {
    private UUID vendorUuid;
    private String vendorName;
    private String emailId;
    private Boolean isActive;
}
