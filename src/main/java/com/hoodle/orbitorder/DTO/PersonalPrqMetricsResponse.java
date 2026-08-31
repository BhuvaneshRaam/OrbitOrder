package com.hoodle.orbitorder.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PersonalPrqMetricsResponse {
    private Long myPendingRequests;
    private Long myRejectedRequests;
    private Long totalApprovedPrqs;
}
