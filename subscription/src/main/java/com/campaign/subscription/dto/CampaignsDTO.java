package com.campaign.subscription.dto;

import com.campaign.subscription.entity.CampaignAggregated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CampaignsDTO {
    List<CampaignAggregated> campaignList;
}
