package com.campaign.subscription.constraint.validator;

import com.campaign.subscription.campaignenum.CategoryType;
import com.campaign.subscription.campaignenum.StateType;
import com.campaign.subscription.dto.SubscriptionRequestDTO;
import com.campaign.subscription.entity.Campaign;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.function.Predicate;

@Slf4j
@Service
public class CampaignValidator {
    public void validateCampaign(Campaign campaign, SubscriptionRequestDTO subscriptionRequestDTO) {
        verifyDates(subscriptionRequestDTO, campaign);
        verifyState(campaign.getState());
    }

    public boolean verifyCategory(String category) {
        Predicate<String> isCategoryValid = categoryofCampaign -> categoryofCampaign.equalsIgnoreCase(CategoryType.BANKING.getType())
                || categoryofCampaign.equalsIgnoreCase(CategoryType.GAMING.getType()) ;
       /* if(isCategoryValid.negate().test(category)) {
            throw new IllegalArgumentException("Subscription not possible as category of 'Campaign' is either Banking or Gaming");
        }*/
        return isCategoryValid.test(category);
    }

    private void verifyState(String state) {
        Predicate<String> isStateValid = stateofCampaign -> stateofCampaign.equalsIgnoreCase(StateType.ACTIVE.getType());
        if(isStateValid.negate().test(state)) {
            throw new IllegalArgumentException("Subscription not possible as state of 'Campaign' is not Active");
        }
    }

    private void verifyDates(SubscriptionRequestDTO subscriptionRequestDTO, Campaign campaign) {
        LocalDate campaign_start_date = campaign.getStartDate();
        LocalDate campaign_end_date = campaign.getEndDate();
        LocalDate subscription_start_date = subscriptionRequestDTO.getStartDate();
        LocalDate subscription_end_date = subscriptionRequestDTO.getEndDate();

        if (subscription_end_date.isAfter(subscription_start_date)) {
            if ((subscription_start_date.isAfter(campaign_start_date) || subscription_start_date.isEqual(campaign_start_date)) &&
                    (subscription_end_date.isBefore(campaign_end_date) || subscription_end_date.isEqual(campaign_end_date))) {
                     log.debug("Dates are aligned");
            } else {
                throw new IllegalArgumentException("Subscription not possible as date range does not fall within Campaign dates");
            }
        } else {
            throw new IllegalArgumentException("Subscription not possible as startDate should be after endDate");
        }
    }

}
