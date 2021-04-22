package com.campaign.subscription.dto;

import com.campaign.subscription.campaignenum.CategoryType;
import com.campaign.subscription.constraint.EnumConstraint;
import lombok.*;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UpdateCampaignRequestDTO {
    @NotNull(message = "{campaign.startDate.notNull}")
    private LocalDate startDate;

    @NotNull(message = "{campaign.endDate.notNull}")
    private LocalDate endDate;

    @NotEmpty(message = "{channels.notNull}")
    private List<@Valid String> channels;

    @NotNull(message = "{campaign.price.notNull}")
    @Positive(message = "{campaign.price.notNegative}")
    private Double price;

    @EnumConstraint(
            acceptedValues = "Gaming | Banking | Movie | Music",
            enumClass = CategoryType.class,
            message = "{campaign.category.valid}"
    )
    private String category;
}
