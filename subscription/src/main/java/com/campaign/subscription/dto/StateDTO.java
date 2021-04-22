package com.campaign.subscription.dto;

import com.campaign.subscription.campaignenum.StateType;
import com.campaign.subscription.constraint.EnumConstraint;
import com.campaign.subscription.entity.CampaignAggregated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StateDTO {
    @EnumConstraint(
            acceptedValues = "Registered | Active | Terminated | Suspended",
            enumClass = StateType.class,
            message = "{campaign.state.valid}"
    )
    private String state;
}
