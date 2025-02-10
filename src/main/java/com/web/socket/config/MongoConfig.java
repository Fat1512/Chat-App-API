package com.web.socket.config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
public class MongoConfig extends AbstractMongoClientConfiguration {

    @Value(value = "${spring.data.mongodb.uri}")
    private String connectionString;

//    @Bean
//    public MongoCustomConversions customConversions(Converter stringToObjectIdConverter) {
//        return new MongoCustomConversions(Arrays.asList(stringToObjectIdConverter));
//    }

    @Bean
    public MongoTransactionManager transactionManager(MongoDatabaseFactory dbFactory) {
        return new MongoTransactionManager(dbFactory);
    }

    @Override
    protected String getDatabaseName() {
        return "chat";
    }

    @Override
    public MongoClient mongoClient() {
        final ConnectionString conn =
                new ConnectionString(connectionString);
        final MongoClientSettings mongoClientSettings = MongoClientSettings.builder()
                .applyConnectionString(conn)
                .build();
        return MongoClients.create(mongoClientSettings);
    }
}







