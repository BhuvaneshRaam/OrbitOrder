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
public class VendorDetailResponse {
    private UUID vendorUuid;
    private String vendorName;
    private String contactPerson;
    private String emailId;
    private String contactPhone;
    private String gstin;
    private String pan;
    private String billingAddress;
    private Boolean isActive;
}
