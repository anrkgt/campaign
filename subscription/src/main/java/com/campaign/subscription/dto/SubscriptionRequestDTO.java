package com.campaign.subscription.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionRequestDTO {
    @NotNull(message = "{campaign.startDate.notNull}")
    private LocalDate startDate;

    @NotNull(message = "{campaign.endDate.notNull}")
    private LocalDate endDate;
}
