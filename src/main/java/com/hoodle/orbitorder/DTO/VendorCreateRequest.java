package com.hoodle.orbitorder.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VendorCreateRequest {
    @NotBlank(message = "Vendor name is required")
    @Size(max = 255, message = "Vendor name must not exceed 255 characters")
    private String vendorName;

    @Size(max = 255, message = "Contact person must not exceed 255 characters")
    private String contactPerson;

    @NotBlank(message = "Email ID is required")
    @Email(message = "Email ID must be valid")
    @Size(max = 255, message = "Email ID must not exceed 255 characters")
    private String emailId;

    @Size(max = 255, message = "Contact phone must not exceed 255 characters")
    private String contactPhone;

    @Size(max = 15, message = "GSTIN must not exceed 15 characters")
    private String gstin;

    @Size(max = 10, message = "PAN must not exceed 10 characters")
    private String pan;
    private String billingAddress;

    @Builder.Default
    private Boolean isActive = true;
}
