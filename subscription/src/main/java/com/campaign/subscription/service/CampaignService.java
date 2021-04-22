package com.campaign.subscription.service;

import com.campaign.subscription.campaignenum.StateType;
import com.campaign.subscription.constraint.validator.CampaignValidator;
import com.campaign.subscription.constraint.validator.UserValidator;
import com.campaign.subscription.dto.CampaignsDTO;
import com.campaign.subscription.dto.SubscriptionRequestDTO;
import com.campaign.subscription.dto.SubscriptionResponseDTO;
import com.campaign.subscription.entity.Campaign;
import com.campaign.subscription.entity.CampaignAggregated;
import com.campaign.subscription.entity.Subscription;
import com.campaign.subscription.entity.User;
import com.campaign.subscription.repository.CampaignRepository;
import com.mongodb.client.result.UpdateResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class CampaignService {
    public static final String SUBSCRIPTIONS = "subscriptions";
    @Autowired
    private CampaignRepository campaignRepository;

    @Autowired
    private SequenceGenerator sequenceGenerator;

    @Autowired
    private UserValidator userValidator;

    @Autowired
    private CampaignValidator campaignValidator;

    public CampaignService(CampaignRepository campaignRepository) {
        this.campaignRepository = campaignRepository;
    }

    public Campaign getCampaignDetails(String id) {
        Campaign campaign = this.campaignRepository.findCampaignById(id);
        return Optional.ofNullable(campaign).orElseThrow(() -> new IllegalArgumentException(String.format("Campaign not found with id::%s", id)));
    }

    public void saveCampaign(Campaign campaign) {
        this.campaignRepository.save(campaign);
        getCampaignDetails(campaign.getId());
    }

    public SubscriptionResponseDTO saveSubscription(String id, SubscriptionRequestDTO subscriptionRequestDTO, User user) {
        preSubscription(id, subscriptionRequestDTO, user);
        Subscription subscription = generateSubscription(id, subscriptionRequestDTO, user);
        SubscriptionResponseDTO subscriptionResponseDTO = new SubscriptionResponseDTO();
        subscriptionResponseDTO.setSubscriptionId(subscription.getSubscriptionId());
        return subscriptionResponseDTO;
    }

    private void preSubscription(String id, SubscriptionRequestDTO subscriptionRequestDTO, User user) {
        userValidator.validateUser(user);
        Campaign campaign = getCampaignDetails(id);
        campaignValidator.validateCampaign(campaign, subscriptionRequestDTO);
        if((userValidator.verifyAge(user.getAge()) <0) &&  campaignValidator.verifyCategory(campaign.getCategory())) {
            throw new IllegalArgumentException("Subscription not possible as category of 'Campaign' is" +
                    " either Banking or Gaming & user's age is below 18");
        }
        campaignValidator.verifyCategory(campaign.getCategory());
    }

    private Subscription generateSubscription(String id, SubscriptionRequestDTO subscriptionRequestDTO, User user) {
        int subscriptionId = sequenceGenerator.getSequence("Sequence");
        Subscription subscription = new Subscription();
        subscription.setUserId(user.getId());
        subscription.setStartDate(subscriptionRequestDTO.getStartDate());
        subscription.setEndDate(subscriptionRequestDTO.getEndDate());
        subscription.setSubscriptionId(Integer.toString(subscriptionId));
        Query query = Query.query(Criteria.where("_id").is(id)
                .and(SUBSCRIPTIONS).not().elemMatch(Criteria.where("userId").is(user.getId())));
        Update update = new Update().push(SUBSCRIPTIONS, subscription);
        Campaign updatedCampaign = this.campaignRepository.updateFirst(query, update, Campaign.class);
        Optional.ofNullable(updatedCampaign).orElseThrow(() -> new IllegalArgumentException("Duplicated Subscription"));
        return subscription;
    }

    public long removeSubscription(String subscriptionId) {
        Query query = Query.query(Criteria.where("subscriptionId").is(subscriptionId));
        Update update = new Update().pull(SUBSCRIPTIONS, query);
        UpdateResult result = campaignRepository.remove(new Query(), update, Campaign.class);
        return result.getModifiedCount();
    }

    public Campaign updateState(String id, String requestedState) {
        Campaign campaign = getCampaignDetails(id);
        StateType current = StateType.valueOf(campaign.getState().toUpperCase());
        List<String> nextState = getNextState(current);
        Optional<String> found = compareAndReturn(requestedState, nextState);
        found.orElseThrow(() -> new IllegalArgumentException("Requested State is not valid"));
        Query query = new Query(Criteria.where("_id").is(id));
        Update update = new Update().set("state", requestedState);
        return campaignRepository.updateFirst(query, update, Campaign.class);
    }

    private Optional<String> compareAndReturn(String requestedState, List<String> nextState) {
        return nextState.stream().filter( e -> e.equalsIgnoreCase(requestedState)).findFirst();
    }

    private List<String> getNextState(StateType current) {
        switch(current) {
            case TERMINATED:
                return Arrays.asList(StateType.TERMINATED.getType()) ;
            case SUSPENDED:
                return  Arrays.asList(StateType.ACTIVE.getType(), StateType.TERMINATED.getType());
            case ACTIVE:
                return  Arrays.asList(StateType.SUSPENDED.getType(), StateType.TERMINATED.getType());
            case REGISTERED:
                return  Arrays.asList(StateType.ACTIVE.getType(), StateType.SUSPENDED.getType(), StateType.TERMINATED.getType());
            default:
                throw new IllegalStateException("Unexpected value for State: " + current);
        }
    }

    public CampaignsDTO getAllActiveCampaigns(String state, String userId) {
        List<CampaignAggregated> campaigns = campaignRepository.findAllActiveCampaignsByUser(state, userId);
        CampaignsDTO campaignsDTO = new CampaignsDTO();
        campaignsDTO.setCampaignList(campaigns);
        return campaignsDTO;
    }

    public void deleteCampaign(String id) {
        Campaign campaign = getCampaignDetails(id);
        this.campaignRepository.delete(campaign);
    }

    public Campaign updateCampaign(Campaign actualCampaign, String id) {
        Campaign existingCampaign =  getCampaignDetails(id);
        actualCampaign.setName(existingCampaign.getName());
        return this.campaignRepository.save(actualCampaign);
    }
}
