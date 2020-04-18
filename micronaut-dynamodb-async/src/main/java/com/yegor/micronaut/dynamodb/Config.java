package com.yegor.micronaut.dynamodb;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsync;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsyncClientBuilder;
import io.micronaut.context.annotation.Bean;
import io.micronaut.context.annotation.Factory;
import io.micronaut.context.env.Environment;

import java.util.Optional;

@Factory
public class Config {

    @Bean
    AmazonDynamoDBAsync dynamoDbAsyncClient(Environment environment) {
        Optional<String> secretKey = environment.get("aws.secretkey", String.class);
        Optional<String> accessKey = environment.get("aws.accesskey", String.class);
        String endpoint = environment.get("dynamo.endpoint", String.class, "http://localhost:8000");
        if (!secretKey.isPresent() || !accessKey.isPresent()) {
            throw new IllegalArgumentException("Aws credentials not provided");
        }
        BasicAWSCredentials credentials = new BasicAWSCredentials(accessKey.get(), secretKey.get());
        AmazonDynamoDBAsyncClientBuilder clientBuilder = AmazonDynamoDBAsyncClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
//                .withRegion(Regions.EU_WEST_1)
                .withEndpointConfiguration(
                        new AwsClientBuilder.EndpointConfiguration(endpoint, null)
                );

        return clientBuilder.build();
    }
}
