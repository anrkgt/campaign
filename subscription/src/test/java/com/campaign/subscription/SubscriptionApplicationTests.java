package com.campaign.subscription;

import com.campaign.subscription.campaignenum.CategoryType;
import com.campaign.subscription.campaignenum.StateType;
import com.campaign.subscription.dto.*;
import com.campaign.subscription.entity.Campaign;
import com.campaign.subscription.entity.User;
import com.campaign.subscription.repository.EmbeddedMongoDbIntegrationTest;
import com.campaign.subscription.service.CampaignService;
import jdk.nashorn.internal.ir.annotations.Ignore;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(EmbeddedMongoDbIntegrationTest.class)
@Profile("test")
class SubscriptionApplicationTests {
	@Autowired
	private TestRestTemplate testRestTemplate;

	@LocalServerPort
	private int port;

	@Autowired
	private CampaignService campaignService;

	@Test
	void testCreateCampaign() {
		//Given
		HttpEntity<CampaignRequestDTO> campaignEntity = createTestCampaign();
		//When
		ResponseEntity<CampaignResponseDTO> createCampaign_Response = this.testRestTemplate.withBasicAuth("admin", "test123")
				.exchange("/api/v1/campaigns", HttpMethod.POST,campaignEntity, CampaignResponseDTO.class);
		//Then
		assertEquals(HttpStatus.CREATED, createCampaign_Response.getStatusCode());
		assertThat(createCampaign_Response).isNotNull();
		assertThat(createCampaign_Response.getBody()).isNotNull();
	}

	private HttpEntity<CampaignRequestDTO> createTestCampaign() {
		CampaignRequestDTO campaignRequestDTO = new CampaignRequestDTO();
		campaignRequestDTO.setCategory("Gaming");
		campaignRequestDTO.setName("Anisa's Campaign");
		campaignRequestDTO.setPrice(new Double(1120));
		campaignRequestDTO.setChannels(Arrays.asList("TV", "Amazon Prime"));
		campaignRequestDTO.setEndDate(LocalDate.now());
		campaignRequestDTO.setStartDate(LocalDate.of(2021, 1, 10));
		HttpEntity<CampaignRequestDTO> campaignEntity = new HttpEntity<>(campaignRequestDTO);
		return campaignEntity;
	}

	private HttpEntity<CampaignRequestDTO> createTestCampaign2() {
		CampaignRequestDTO campaignRequestDTO = new CampaignRequestDTO();
		campaignRequestDTO.setCategory("Gaming");
		campaignRequestDTO.setName("Test's Campaign");
		campaignRequestDTO.setPrice(new Double(1120));
		campaignRequestDTO.setChannels(Arrays.asList("TV"));
		campaignRequestDTO.setEndDate(LocalDate.now());
		campaignRequestDTO.setStartDate(LocalDate.of(2021, 2, 11));
		HttpEntity<CampaignRequestDTO> campaignEntity = new HttpEntity<>(campaignRequestDTO);
		return campaignEntity;
	}

	@Test
	void testGetCampaign() {
		//Given
		HttpEntity<CampaignRequestDTO> campaignEntity = createTestCampaign();
		ResponseEntity<CampaignResponseDTO> createCampaign_Response = this.testRestTemplate.withBasicAuth("admin", "test123")
				.exchange("/api/v1/campaigns", HttpMethod.POST,campaignEntity, CampaignResponseDTO.class);
		String campaignId = createCampaign_Response.getBody().getCampaignId();
		//When
		ResponseEntity<Campaign> getCampaign_Response = this.testRestTemplate.withBasicAuth("admin", "test123")
				.exchange("/api/v1/campaigns/" +campaignId , HttpMethod.GET,null, Campaign.class);
		//Then
		Campaign campaign = getCampaign_Response.getBody();
		assertThat(getCampaign_Response).isNotNull();
		assertThat(getCampaign_Response.getBody()).isNotNull();
		assertEquals(HttpStatus.OK, getCampaign_Response.getStatusCode());
		assertEquals(campaign.getName(), campaignEntity.getBody().getName());
		assertEquals(campaign.getChannels(), campaignEntity.getBody().getChannels());
		assertEquals(campaign.getCategory(), campaignEntity.getBody().getCategory());
		assertEquals(campaign.getStartDate(), campaignEntity.getBody().getStartDate());
		assertEquals(campaign.getEndDate(), campaignEntity.getBody().getEndDate());
		assertEquals(campaign.getId(), campaignId);
	}

	@Test
	void testDeleteCampaign() {
		//Given
		HttpEntity<CampaignRequestDTO> campaignEntity = createTestCampaign();
		ResponseEntity<CampaignResponseDTO> createCampaign_Response = this.testRestTemplate.withBasicAuth("admin", "test123")
				.exchange("/api/v1/campaigns", HttpMethod.POST,campaignEntity, CampaignResponseDTO.class);
		String campaignId = createCampaign_Response.getBody().getCampaignId();

		//When
		ResponseEntity<Void> deleteCampaign_Response = this.testRestTemplate.withBasicAuth("admin", "test123")
				.exchange("/api/v1/campaigns/" +campaignId , HttpMethod.DELETE,null, Void.class);

		//Then
		assertEquals(HttpStatus.OK, deleteCampaign_Response.getStatusCode());
	}

	@Test
	void testUpdateCampaign() {
		//Given
		HttpEntity<CampaignRequestDTO> campaignEntity = createTestCampaign();
		ResponseEntity<CampaignResponseDTO> createCampaign_Response = this.testRestTemplate.withBasicAuth("admin", "test123")
				.exchange("/api/v1/campaigns", HttpMethod.POST,campaignEntity, CampaignResponseDTO.class);
		String campaignId = createCampaign_Response.getBody().getCampaignId();

		UpdateCampaignRequestDTO updateCampaignRequestDTO = new UpdateCampaignRequestDTO();
		updateCampaignRequestDTO.setCategory("Music");
		updateCampaignRequestDTO.setChannels(Arrays.asList("Songs"));
		updateCampaignRequestDTO.setPrice(new Double(400));
		updateCampaignRequestDTO.setEndDate(LocalDate.now());
		HttpEntity<UpdateCampaignRequestDTO> campaign_http_entity = new HttpEntity<>(updateCampaignRequestDTO);
		//When
		ResponseEntity<GetCampaignResponseDTO> updateCampaign_Response = this.testRestTemplate.withBasicAuth("admin", "test123")
				.exchange("/api/v1/campaigns/" +campaignId , HttpMethod.PUT,campaign_http_entity, GetCampaignResponseDTO.class);
		//Then
		ResponseEntity<GetCampaignResponseDTO> getCampaign_Response = this.testRestTemplate.withBasicAuth("admin", "test123")
				.exchange("/api/v1/campaigns/" +campaignId , HttpMethod.GET,null, GetCampaignResponseDTO.class);
		GetCampaignResponseDTO db_campaign = updateCampaign_Response.getBody();

		assertEquals(HttpStatus.CREATED, createCampaign_Response.getStatusCode());
		assertEquals(HttpStatus.BAD_REQUEST, updateCampaign_Response.getStatusCode());
		assertEquals(HttpStatus.OK, getCampaign_Response.getStatusCode());
	}

	@Test
	void testUpdateCampaign_Valid() {
		//Given
		HttpEntity<CampaignRequestDTO> campaignEntity = createTestCampaign();
		ResponseEntity<CampaignResponseDTO> createCampaign_Response = this.testRestTemplate.withBasicAuth("admin", "test123")
				.exchange("/api/v1/campaigns", HttpMethod.POST,campaignEntity, CampaignResponseDTO.class);
		String campaignId = createCampaign_Response.getBody().getCampaignId();

		UpdateCampaignRequestDTO updateCampaignRequestDTO = new UpdateCampaignRequestDTO();
		updateCampaignRequestDTO.setCategory("Music");
		updateCampaignRequestDTO.setChannels(Arrays.asList("Songs"));
		updateCampaignRequestDTO.setPrice(new Double(400));
		updateCampaignRequestDTO.setStartDate(LocalDate.now());
		updateCampaignRequestDTO.setEndDate(LocalDate.now());
		HttpEntity<UpdateCampaignRequestDTO> campaign_http_entity = new HttpEntity<>(updateCampaignRequestDTO);
		//When
		ResponseEntity<GetCampaignResponseDTO> updateCampaign_Response = this.testRestTemplate.withBasicAuth("admin", "test123")
				.exchange("/api/v1/campaigns/" +campaignId , HttpMethod.PUT,campaign_http_entity, GetCampaignResponseDTO.class);
		//Then
		ResponseEntity<GetCampaignResponseDTO> getCampaign_Response = this.testRestTemplate.withBasicAuth("admin", "test123")
				.exchange("/api/v1/campaigns/" +campaignId , HttpMethod.GET,null, GetCampaignResponseDTO.class);
		GetCampaignResponseDTO db_campaign = updateCampaign_Response.getBody();

		assertEquals(HttpStatus.CREATED, createCampaign_Response.getStatusCode());
		assertEquals(HttpStatus.OK, updateCampaign_Response.getStatusCode());
		assertEquals(HttpStatus.OK, getCampaign_Response.getStatusCode());

		assertEquals(updateCampaignRequestDTO.getCategory(), db_campaign.getCategory());
		assertEquals(updateCampaignRequestDTO.getChannels(), db_campaign.getChannels());
		assertEquals(updateCampaignRequestDTO.getEndDate(), db_campaign.getEndDate());
		assertEquals(updateCampaignRequestDTO.getPrice(), db_campaign.getPrice());
		assertEquals(campaignId, db_campaign.getId());
	}

	@Test
	void testManageStates() {
		//Given
		HttpEntity<CampaignRequestDTO> campaignEntity = createTestCampaign();
		ResponseEntity<CampaignResponseDTO> createCampaign_Response = this.testRestTemplate.withBasicAuth("admin", "test123")
				.exchange("/api/v1/campaigns", HttpMethod.POST, campaignEntity, CampaignResponseDTO.class);
		String campaignId = createCampaign_Response.getBody().getCampaignId();
		//When
		StateDTO stateDTO = new StateDTO();
		stateDTO.setState(StateType.ACTIVE.getType());
		HttpEntity<StateDTO> stateEntity = new HttpEntity<>(stateDTO);
		ResponseEntity<GetCampaignResponseDTO> manageStates_Response = this.testRestTemplate.withBasicAuth("admin", "test123")
				.exchange("/api/v1/campaigns/" +campaignId+"/states", HttpMethod.PUT, stateEntity, GetCampaignResponseDTO.class);
		//Then
		GetCampaignResponseDTO db_campaign = manageStates_Response.getBody();

		assertEquals(HttpStatus.CREATED, createCampaign_Response.getStatusCode());
		assertEquals(HttpStatus.OK, manageStates_Response.getStatusCode());

		assertEquals(stateDTO.getState(), db_campaign.getState());
	}

	@Test
	void testManageStates_Invalid() {
		//Given
		HttpEntity<CampaignRequestDTO> campaignEntity = createTestCampaign();
		ResponseEntity<CampaignResponseDTO> createCampaign_Response = this.testRestTemplate.withBasicAuth("admin", "test123")
				.exchange("/api/v1/campaigns", HttpMethod.POST, campaignEntity, CampaignResponseDTO.class);
		String campaignId = createCampaign_Response.getBody().getCampaignId();
		//When
		StateDTO stateDTO = new StateDTO();
		stateDTO.setState("Terminated");
		HttpEntity<StateDTO> stateEntity = new HttpEntity<>(stateDTO);
		ResponseEntity<GetCampaignResponseDTO> manageStates_Response = this.testRestTemplate.withBasicAuth("admin", "test123")
				.exchange("/api/v1/campaigns/" +campaignId+"/states", HttpMethod.PUT, stateEntity, GetCampaignResponseDTO.class);

		assertEquals(HttpStatus.OK, manageStates_Response.getStatusCode());

		stateDTO.setState("Active");
		stateEntity = new HttpEntity<>(stateDTO);
		manageStates_Response = this.testRestTemplate.withBasicAuth("admin", "test123")
				.exchange("/api/v1/campaigns/" +campaignId+"/states", HttpMethod.PUT, stateEntity, GetCampaignResponseDTO.class);

		//Then
		assertEquals(HttpStatus.CREATED, createCampaign_Response.getStatusCode());
		assertEquals(HttpStatus.BAD_REQUEST, manageStates_Response.getStatusCode());

	}

	@Test
	void testUnsubscribeCampaign() throws Exception {
		//Given
		HttpEntity<CampaignRequestDTO> campaignEntity = createTestCampaign();
		ResponseEntity<CampaignResponseDTO> createCampaign_Response = this.testRestTemplate.withBasicAuth("admin", "test123")
				.exchange("/api/v1/campaigns", HttpMethod.POST, campaignEntity, CampaignResponseDTO.class);
		String campaignId = createCampaign_Response.getBody().getCampaignId();

		StateDTO stateDTO = new StateDTO();
		stateDTO.setState(StateType.ACTIVE.getType());
		HttpEntity<StateDTO> stateEntity = new HttpEntity<>(stateDTO);
		ResponseEntity<GetCampaignResponseDTO> manageStates_Response = this.testRestTemplate.withBasicAuth("admin", "test123")
				.exchange("/api/v1/campaigns/" +campaignId+"/states", HttpMethod.PUT, stateEntity, GetCampaignResponseDTO.class);

		SubscriptionRequestDTO subscriptionRequestDTO = new SubscriptionRequestDTO();
		subscriptionRequestDTO.setStartDate(LocalDate.now().minusDays(2));
		subscriptionRequestDTO.setEndDate(LocalDate.now());

		User user = new User();
		user.setAge(20);
		user.setEmail("abc@facebook.com");
		user.setId("1");
		user.setState("Active");
		user.setPhoneNumber("0919196767");
		SubscriptionResponseDTO subscriptionResponseDTO = campaignService.saveSubscription(campaignId,subscriptionRequestDTO, user );
		String subscriptionId = subscriptionResponseDTO.getSubscriptionId();

		 //When
		ResponseEntity<String> unsubscribeCampaign_Response = this.testRestTemplate.withBasicAuth("admin", "test123")
				.exchange("/api/v1/campaigns/" +campaignId+"/subscriptions/"+subscriptionId+"/unsubscribe",
						HttpMethod.PUT, null, String.class);

		//Then
		assertEquals(HttpStatus.OK, unsubscribeCampaign_Response.getStatusCode());
		assertEquals(HttpStatus.CREATED, createCampaign_Response.getStatusCode());
	}

	@Test
	void testGetActiveCampaign() throws Exception {
		//Given
		HttpEntity<CampaignRequestDTO> campaignEntity = createTestCampaign();
		ResponseEntity<CampaignResponseDTO> createCampaign_Response = this.testRestTemplate.withBasicAuth("admin", "test123")
				.exchange("/api/v1/campaigns", HttpMethod.POST, campaignEntity, CampaignResponseDTO.class);
		String campaignId = createCampaign_Response.getBody().getCampaignId();

		StateDTO stateDTO = new StateDTO();
		stateDTO.setState(StateType.ACTIVE.getType());
		HttpEntity<StateDTO> stateEntity = new HttpEntity<>(stateDTO);
		ResponseEntity<GetCampaignResponseDTO> manageStates_Response = this.testRestTemplate.withBasicAuth("admin", "test123")
				.exchange("/api/v1/campaigns/" +campaignId+"/states", HttpMethod.PUT, stateEntity, GetCampaignResponseDTO.class);

		SubscriptionRequestDTO subscriptionRequestDTO = new SubscriptionRequestDTO();
		subscriptionRequestDTO.setStartDate(LocalDate.now().minusDays(2));
		subscriptionRequestDTO.setEndDate(LocalDate.now());

		User user = new User();
		user.setAge(20);
		user.setEmail("abc@facebook.com");
		user.setId("1");
		user.setState("Active");
		user.setPhoneNumber("0919196767");
		SubscriptionResponseDTO subscriptionResponseDTO = campaignService.saveSubscription(campaignId,subscriptionRequestDTO, user );
		String subscriptionId = subscriptionResponseDTO.getSubscriptionId();


		campaignEntity = createTestCampaign2();
		createCampaign_Response = this.testRestTemplate.withBasicAuth("admin", "test123")
				.exchange("/api/v1/campaigns", HttpMethod.POST, campaignEntity, CampaignResponseDTO.class);
		campaignId = createCampaign_Response.getBody().getCampaignId();
		stateDTO = new StateDTO();
		stateDTO.setState(StateType.ACTIVE.getType());
		stateEntity = new HttpEntity<>(stateDTO);
		manageStates_Response = this.testRestTemplate.withBasicAuth("admin", "test123")
				.exchange("/api/v1/campaigns/" +campaignId+"/states", HttpMethod.PUT, stateEntity, GetCampaignResponseDTO.class);

		subscriptionRequestDTO = new SubscriptionRequestDTO();
		subscriptionRequestDTO.setStartDate(LocalDate.now().minusDays(2));
		subscriptionRequestDTO.setEndDate(LocalDate.now());

		subscriptionResponseDTO = campaignService.saveSubscription(campaignId,subscriptionRequestDTO, user );
		subscriptionId = subscriptionResponseDTO.getSubscriptionId();


		//When
		ResponseEntity<CampaignsDTO> getActiveCampaigns_Response = this.testRestTemplate.withBasicAuth("admin", "test123")
				.exchange("/api/v1/campaigns/users/1?status=Active",
						HttpMethod.GET, null, CampaignsDTO.class);

		//Then
		assertEquals(HttpStatus.OK, getActiveCampaigns_Response.getStatusCode());
		CampaignsDTO campaignsDTO = getActiveCampaigns_Response.getBody();
		assertEquals(2, campaignsDTO.getCampaignList().size());
		assertEquals(LocalDate.of(2021, 1, 10), campaignsDTO.getCampaignList().get(0).getStartDate());
		assertEquals(LocalDate.of(2021, 2, 11), campaignsDTO.getCampaignList().get(1).getStartDate());
		assertEquals(HttpStatus.CREATED, createCampaign_Response.getStatusCode());

	}

	@Test
	void testSubscribeCampaign_InvalidCampaignState() throws Exception {
		//Given
		HttpEntity<CampaignRequestDTO> campaignEntity = createTestCampaign();
		ResponseEntity<CampaignResponseDTO> createCampaign_Response = this.testRestTemplate.withBasicAuth("admin", "test123")
				.exchange("/api/v1/campaigns", HttpMethod.POST, campaignEntity, CampaignResponseDTO.class);
		String campaignId = createCampaign_Response.getBody().getCampaignId();

		//When
		SubscriptionRequestDTO subscriptionRequestDTO = new SubscriptionRequestDTO();
		subscriptionRequestDTO.setStartDate(LocalDate.now().minusDays(2));
		subscriptionRequestDTO.setEndDate(LocalDate.now());

		User user = new User();
		user.setAge(20);
		user.setEmail("abc@facebook.com");
		user.setId("1");
		user.setState("Active");
		user.setPhoneNumber("0919196767");

		//Then
		String state = "Registered";
		Exception exception = assertThrows(IllegalArgumentException.class,
				() -> campaignService.saveSubscription(campaignId,subscriptionRequestDTO, user) );
		assertEquals("Subscription not possible as state of 'Campaign' is not Active",
				exception.getMessage());

	}

	@Test
	void testSubscribeCampaign_InvalidAgeCategory() throws Exception {
		//Given
		HttpEntity<CampaignRequestDTO> campaignEntity = createTestCampaign();
		campaignEntity.getBody().setCategory(CategoryType.BANKING.getType());
		ResponseEntity<CampaignResponseDTO> createCampaign_Response = this.testRestTemplate.withBasicAuth("admin", "test123")
				.exchange("/api/v1/campaigns", HttpMethod.POST, campaignEntity, CampaignResponseDTO.class);
		String campaignId = createCampaign_Response.getBody().getCampaignId();

		StateDTO stateDTO = new StateDTO();
		stateDTO.setState(StateType.ACTIVE.getType());
		HttpEntity<StateDTO> stateEntity = new HttpEntity<>(stateDTO);
		ResponseEntity<GetCampaignResponseDTO> manageStates_Response = this.testRestTemplate.withBasicAuth("admin", "test123")
				.exchange("/api/v1/campaigns/" +campaignId+"/states", HttpMethod.PUT, stateEntity, GetCampaignResponseDTO.class);

		//When
		SubscriptionRequestDTO subscriptionRequestDTO = new SubscriptionRequestDTO();
		subscriptionRequestDTO.setStartDate(LocalDate.now().minusDays(2));
		subscriptionRequestDTO.setEndDate(LocalDate.now());

		User user = new User();
		user.setAge(17);
		user.setEmail("abc@facebook.com");
		user.setId("1");
		user.setState("Active");
		user.setPhoneNumber("0919196767");

		//Then
		String state = "Registered";
		Exception exception = assertThrows(IllegalArgumentException.class,
				() -> campaignService.saveSubscription(campaignId,subscriptionRequestDTO, user) );
		assertEquals("Subscription not possible as category of 'Campaign' is" +
						" either Banking or Gaming & user's age is below 18",
				exception.getMessage());

	}

	@Test
	void testSubscribeCampaign_valid() throws Exception {
		//Given
		HttpEntity<CampaignRequestDTO> campaignEntity = createTestCampaign();
		ResponseEntity<CampaignResponseDTO> createCampaign_Response = this.testRestTemplate.withBasicAuth("admin", "test123")
				.exchange("/api/v1/campaigns", HttpMethod.POST, campaignEntity, CampaignResponseDTO.class);
		String campaignId = createCampaign_Response.getBody().getCampaignId();

		StateDTO stateDTO = new StateDTO();
		stateDTO.setState(StateType.ACTIVE.getType());
		HttpEntity<StateDTO> stateEntity = new HttpEntity<>(stateDTO);
		ResponseEntity<GetCampaignResponseDTO> manageStates_Response = this.testRestTemplate.withBasicAuth("admin", "test123")
				.exchange("/api/v1/campaigns/" +campaignId+"/states", HttpMethod.PUT, stateEntity, GetCampaignResponseDTO.class);

		//When
		SubscriptionRequestDTO subscriptionRequestDTO = new SubscriptionRequestDTO();
		subscriptionRequestDTO.setStartDate(LocalDate.now().minusDays(2));
		subscriptionRequestDTO.setEndDate(LocalDate.now());

		User user = new User();
		user.setAge(20);
		user.setEmail("abc@facebook.com");
		user.setId("1");
		user.setState("Active");
		user.setPhoneNumber("0919196767");
		SubscriptionResponseDTO subscriptionResponseDTO = campaignService.saveSubscription(campaignId,subscriptionRequestDTO, user );

		//Then
		assertNotNull(subscriptionResponseDTO.getSubscriptionId());

	}

	@ParameterizedTest
	@EnumSource(StateType.class )
	void states_campaign(StateType stateType) {
		assertNotNull(campaignService.getNextState(stateType));
	}

}
