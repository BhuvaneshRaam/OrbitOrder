package com.hoodle.orbitorder.DTO;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DashboardSummaryResponse {
    private PersonalPrqMetricsResponse personalPrqMetrics;
    private List<PrqSummaryResponse> recentPrqs;
    private ApprovalMetricsResponse approvalMetrics;
    private List<PrqSummaryResponse> bottleneckPrqs;
    private PoMetricsResponse poMetrics;
    private List<POSummaryResponse> recentPos;
    private VendorMetricsResponse vendorMetrics;
}
