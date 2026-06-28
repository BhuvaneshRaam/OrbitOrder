package com.hoodle.orbitorder.DTO;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PrRequest {
    private String department;
    private String remarks;
    private List<PrItemRequest> items;
}
