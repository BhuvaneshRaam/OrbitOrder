package com.hoodle.orbitorder.DTO;

import lombok.Data;

import java.util.List;

@Data
public class PoUpdateRequest {
    private String vendorName;
    private String vendorEmail;

    private List<PoItemUpdateRequest> items;
}
