package com.example.dynamo_spring;

import com.amazonaws.services.dynamodbv2.local.main.ServerRunner;
import com.amazonaws.services.dynamodbv2.local.server.DynamoDBProxyServer;
import org.junit.rules.ExternalResource;


public class LocalDynamoDbRule extends ExternalResource {

    protected DynamoDBProxyServer server;

    public LocalDynamoDbRule() {
        System.setProperty("sqlite4java.library.path", "target/native-libs");
    }

    @Override
    protected void before() throws Exception {
        this.server = ServerRunner.createServerFromCommandLineArgs(new String[]{"-inMemory", "-port", "8000"});
        server.start();
    }

    @Override
    protected void after() {
        this.stopUnchecked(server);
    }

    protected void stopUnchecked(DynamoDBProxyServer dynamoDbServer) {
        try {
            dynamoDbServer.stop();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
