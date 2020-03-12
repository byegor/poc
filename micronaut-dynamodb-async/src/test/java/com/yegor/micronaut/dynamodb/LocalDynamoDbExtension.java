package com.yegor.micronaut.dynamodb;

import com.amazonaws.services.dynamodbv2.local.main.ServerRunner;
import com.amazonaws.services.dynamodbv2.local.server.DynamoDBProxyServer;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.rules.ExternalResource;

public class LocalDynamoDbExtension implements AfterAllCallback, BeforeAllCallback {

    protected DynamoDBProxyServer server;

    public LocalDynamoDbExtension() {
        //here we set the path from "outputDirectory" of maven-dependency-plugin
        System.setProperty("sqlite4java.library.path", "target/native-libs");
    }

    @Override
    public void afterAll(ExtensionContext extensionContext) throws Exception {
        stopUnchecked(server);
    }

    @Override
    public void beforeAll(ExtensionContext extensionContext) throws Exception {
        this.server = ServerRunner
                .createServerFromCommandLineArgs(new String[]{"-inMemory", "-port", "8000"});
        server.start();
    }


    protected void stopUnchecked(DynamoDBProxyServer dynamoDbServer) {
        try {
            dynamoDbServer.stop();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
