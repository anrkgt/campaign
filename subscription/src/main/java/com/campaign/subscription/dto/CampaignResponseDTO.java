package com.campaign.subscription.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CampaignResponseDTO {
    @JsonProperty("campaignId")
    private String campaignId;
}
