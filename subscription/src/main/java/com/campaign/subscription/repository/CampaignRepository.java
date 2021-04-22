package com.campaign.subscription.repository;

import com.campaign.subscription.entity.Campaign;
import com.campaign.subscription.entity.CampaignAggregated;
import com.campaign.subscription.template.TemplateMongo;
import com.mongodb.client.result.UpdateResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

@Repository
public class CampaignRepository {
    public static final String START_DATE = "startDate";
    @Autowired
    private TemplateMongo templateMongo;

    public CampaignRepository(TemplateMongo templateMongo) {
        this.templateMongo = templateMongo;
    }

    public Campaign save(Campaign entity) {
        templateMongo.getTemplateMongo().save(entity);
        return entity;
    }

    public Campaign findCampaignById(String id) {
        return templateMongo.getTemplateMongo().findById(id, Campaign.class);
    }

    public Campaign removeCampaign(Campaign campaign) {
        templateMongo.getTemplateMongo().remove(campaign);
        return campaign;
    }

    public List<Campaign> findAllCampaigns() {
        return templateMongo.getTemplateMongo().findAll(Campaign.class);
    }

    public List<CampaignAggregated> findAllActiveCampaignsByUser(String state, String userId) {
        Aggregation agg = Aggregation.newAggregation(
                unwind("subscriptions"),
                match(Criteria.where("state").is(state).and("subscriptions.userId").is(userId)),
                group(  "name","category", "channels", "price", START_DATE, "endDate"),
                project("name","category", "channels", "price", START_DATE, "endDate"),
                sort(Sort.Direction.ASC, START_DATE));

        return templateMongo.getTemplateMongo().aggregate(agg, Campaign.class, CampaignAggregated.class).getMappedResults();
    }

    public UpdateResult remove(Query query, Update update, Class<Campaign> campaignClass) {
        return templateMongo.getTemplateMongo().updateMulti(query, update, Campaign.class);
    }

    public Campaign updateFirst(Query query, Update update, Class<Campaign> campaignClass) {
        FindAndModifyOptions options = new FindAndModifyOptions();
        options.returnNew(true);
        return templateMongo.getTemplateMongo().findAndModify(query, update, options, Campaign.class);
    }

    public void delete(Campaign campaign) {
        templateMongo.getTemplateMongo().remove(campaign);
    }
}
