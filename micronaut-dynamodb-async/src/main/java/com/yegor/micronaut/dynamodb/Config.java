package com.yegor.micronaut.dynamodb;

import io.micronaut.context.annotation.Bean;
import io.micronaut.context.annotation.Factory;
import io.micronaut.context.env.Environment;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClientBuilder;

import java.net.URI;
import java.util.Optional;

@Factory
public class Config {

    @Bean
    DynamoDbAsyncClient dynamoDbAsyncClient(Environment environment) {
        Optional<String> secretKey = environment.get("aws.secretkey", String.class);
        Optional<String> accessKey = environment.get("aws.accesskey", String.class);
        if (secretKey.isEmpty() || accessKey.isEmpty()) {
            throw new IllegalArgumentException("Aws credentials not provided");
        }
        AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey.get(), secretKey.get());
        DynamoDbAsyncClientBuilder clientBuilder = DynamoDbAsyncClient.builder()
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .region(Region.EU_WEST_1)
                .endpointOverride(URI.create("http://localhost:8000"));

        return clientBuilder.build();
    }
}
