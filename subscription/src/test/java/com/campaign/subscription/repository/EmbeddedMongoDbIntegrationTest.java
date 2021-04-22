package com.campaign.subscription.repository;

import com.mongodb.client.MongoClients;
import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.IMongodConfig;
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.runtime.Network;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.MongoTemplate;

@Profile("test")
@TestConfiguration
public class EmbeddedMongoDbIntegrationTest {
   private static final String CONNECTION_STRING = "mongodb://%s:%d";
    public static final String LOCALHOST = "localhost";
    public static final int PORT = 27017;
    public static final String DATABASE_NAME = "testCampaignDB";
    private MongoTemplate mongoTemplate;
    private MongodExecutable mongodExecutable;

    @BeforeEach
    public void beforeEach() throws Exception {
        String ip = LOCALHOST;
        int port = PORT;
        IMongodConfig mongodConfig = new MongodConfigBuilder().version(Version.Main.PRODUCTION)
                .net(new Net(ip, port, Network.localhostIsIPv6()))
                .build();
        MongodStarter starter = MongodStarter.getDefaultInstance();
        mongodExecutable = starter.prepare(mongodConfig);
        mongodExecutable.start();
        mongoTemplate = new MongoTemplate(MongoClients.create(String.format(CONNECTION_STRING, ip, port)), DATABASE_NAME);
    }

    @AfterEach
    public void afterEach() throws Exception {
        mongodExecutable.stop();
    }
}
