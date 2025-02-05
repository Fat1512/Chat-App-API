package com.web.socket.config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
public class MongoConfig extends AbstractMongoClientConfiguration {
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
        final ConnectionString connectionString =
                new ConnectionString("mongodb://adminUser:1@121.0.0.1:27017,127.0.0.1:27018,127.0.0.1:27019/chat?authSource=chat&replicaSet=rs0");
        final MongoClientSettings mongoClientSettings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .build();
        return MongoClients.create(mongoClientSettings);
    }
}







