package com.campaign.subscription.template;

import com.mongodb.client.MongoClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.MongoTemplate;

@Profile("!test")
@Configuration
public class TemplateMongo {
    @Value("${mongodb.datasource}")
    private String connectionUrl;

    public MongoTemplate getTemplateMongo() {
              return new MongoTemplate(MongoClients.create(String.format(connectionUrl)), "Campaigns");
    }
}
