package com.campaign.subscription.dto;

import com.campaign.subscription.entity.Subscription;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDate;
import java.util.List;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@ToString
public class GetCampaignResponseDTO {
        private String Id;

        private String name;

        private String state;

        @JsonProperty("startDate")
        private LocalDate startDate;

        @JsonProperty("endDate")
        private LocalDate endDate;

        private Double price;

        private String category;

        private List<String> channels;

        List<Subscription> subscriptions;
}
