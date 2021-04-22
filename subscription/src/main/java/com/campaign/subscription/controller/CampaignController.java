package com.campaign.subscription.controller;

import com.campaign.subscription.campaignenum.StateType;
import com.campaign.subscription.dto.*;
import com.campaign.subscription.entity.Campaign;
import com.campaign.subscription.entity.User;
import com.campaign.subscription.service.CampaignService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;

@Slf4j
@Validated
@RestController
@RequestMapping("/api/v1/campaigns")
@Tag(name = "Campaigns", description = "Campaign Management")
public class CampaignController {
    @Autowired
    private CampaignService campaignService;

    @Autowired
    private RestTemplate restTemplate;

    public CampaignController(CampaignService campaignService) {
        this.campaignService = campaignService;
    }

    @PostMapping
    @Operation(summary = "Create new campaigns" , description = "API to create new campaigns for CMP", method = "Post")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", description = "Not authorized"),
            @ApiResponse(responseCode = "201", description = "Campaign created"),
            @ApiResponse(responseCode = "400", description = "Bad request")
    })
    public ResponseEntity<CampaignResponseDTO> createCampaign(@Valid @RequestBody CampaignRequestDTO campaignRequestDTO) {
        log.info("Campaign creation requested:: " + campaignRequestDTO.toString());
        ObjectMapper objectMapper = getObjectMapper();
        Campaign campaign = objectMapper.convertValue(campaignRequestDTO, Campaign.class);
        campaign.setState(StateType.REGISTERED.getType());
        this.campaignService.saveCampaign(campaign);
        CampaignResponseDTO campaignResponseDTO = new CampaignResponseDTO();
        campaignResponseDTO.setCampaignId(campaign.getId());
        log.info("Campaign creation requested processed :: " + campaignResponseDTO.toString());
        return new ResponseEntity<>(campaignResponseDTO, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get campaign by id" , description = "API to fetch campaign with a given id for Campaign", method = "Get")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", description = "Not authorized"),
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "404", description = "Not found"),
    })
    public ResponseEntity<GetCampaignResponseDTO> getCampaign(@PathVariable("id") String id){
        Campaign campaign = campaignService.getCampaignDetails(id);
        ObjectMapper objectMapper = getObjectMapper();
        GetCampaignResponseDTO getCampaignResponseDTO = objectMapper.convertValue(campaign, GetCampaignResponseDTO.class);
        return new ResponseEntity<>(getCampaignResponseDTO, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update existing campaign" , description = "API to update campaign details", method = "Put")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", description = "Not authorized"),
            @ApiResponse(responseCode = "200", description = "Record updated successfully"),
            @ApiResponse(responseCode = "404", description = "Record not found")
    })
    public ResponseEntity<GetCampaignResponseDTO> updateCampaign(@Valid @RequestBody UpdateCampaignRequestDTO updateCampaignRequestDTO,
                                                                 @PathVariable("id") String id){
        log.info("Campaign update requested:: " + updateCampaignRequestDTO.toString());
        Campaign actualCampaign = new Campaign();
        actualCampaign.setId(id);
        BeanUtils.copyProperties(updateCampaignRequestDTO, actualCampaign);
        Campaign campaign = this.campaignService.updateCampaign(actualCampaign, id);
        log.info("Campaign update processed:: " + formatResponse(campaign).toString());
        return new ResponseEntity<>(formatResponse(campaign), HttpStatus.OK);
    }

    private ObjectMapper getObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return objectMapper;
    }

    @PutMapping("/{id}/subscriptions/users/{user-id}")
    @Operation(summary = "Subscribe" , description = "API to subscribe for a Campaign", method = "Put")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", description = "Not authorized"),
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "400", description = "Bad request")
    })
    public ResponseEntity<SubscriptionResponseDTO> subscribeCampaign(@PathVariable("id") String id, @PathVariable("user-id") String userId,
                                                                     @Valid @RequestBody SubscriptionRequestDTO subscriptionRequestDTO) {
        URI targetUrl= UriComponentsBuilder.fromUriString("http://user/api/v1/users")
                .path("/"+userId)
                .build()
                .encode()
                .toUri();

        ResponseEntity<User> actualUser = restTemplate.getForEntity(targetUrl, User.class);
        SubscriptionResponseDTO subscriptionResponseDTO = this.campaignService.saveSubscription(id, subscriptionRequestDTO, actualUser.getBody());
        return new ResponseEntity<>(subscriptionResponseDTO , HttpStatus.CREATED);
    }

    @PutMapping("/{id}/subscriptions/{subscription_id}/unsubscribe")
    @Operation(summary = "Unsubscribe" , description = "API to unsubscribe for a Campaign", method = "Put")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", description = "Not authorized"),
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "204", description = "Success with no response"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "400", description = "Bad request")
    })
    public ResponseEntity<String> unsubscribeCampaign(@PathVariable("id") String id, @PathVariable("subscription_id") String subscriptionId ) {
        long noOfUpdatedDocuments = this.campaignService.removeSubscription(subscriptionId);
        log.info("Unsubscribed  : " + noOfUpdatedDocuments);
        return noOfUpdatedDocuments > 0 ? new ResponseEntity<>(HttpStatus.OK) :
                new ResponseEntity<>("Subscription id not found" , HttpStatus.NOT_FOUND);

    }

    @PutMapping("/{id}/states")
    @Operation(summary = "Manage States of Campaign" , description = "API to manage states for a Campaign", method = "Put")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", description = "Not authorized"),
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "400", description = "Bad request")
    })
    public ResponseEntity<GetCampaignResponseDTO> manageState(@PathVariable("id") String id, @Valid @RequestBody StateDTO stateDTO) {
        Campaign campaign = this.campaignService.updateState(id, stateDTO.getState());
        return new ResponseEntity<>(formatResponse(campaign),HttpStatus.OK);
    }

    @GetMapping("/users/{user-id}")
    @Operation(summary = "Get all campaigns" , description = "API to fetch campaigns", method = "Get")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", description = "Not authorized"),
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "404", description = "Not found"),
    })
    public ResponseEntity<CampaignsDTO> getCampaigns(@PathVariable("user-id") String userId, @RequestParam("status") String state){
        CampaignsDTO campaignsDTO = campaignService.getAllActiveCampaigns(state, userId);
        return new ResponseEntity<>(campaignsDTO, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete specific campaign" , description = "API to delete campaign with given id", method = "Delete")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", description = "Not authorized"),
            @ApiResponse(responseCode = "200", description = "Campaign deleted"),
            @ApiResponse(responseCode = "404", description = "Not found")
    })
    public void removeCampaign(@PathVariable("id") String id) {
        this.campaignService.deleteCampaign(id);
    }

    private GetCampaignResponseDTO formatResponse(Campaign campaign) {
        ObjectMapper objectMapper = getObjectMapper();
        return objectMapper.convertValue(campaign, GetCampaignResponseDTO.class);
    }

}
