package com.test;

import org.junit.jupiter.api.TestInstance;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext
public class MongoDBContainerConfig {

    private static final Integer MONGO_PORT = 27017;

    @Container
    private static final MongoDBContainer mongoDBContainer = new MongoDBContainer(DockerImageName
        .parse("mongo:8.0.5")
        .asCompatibleSubstituteFor("mongo"))
        .withExposedPorts(MONGO_PORT)
        .withReuse(true)
        .withCommand("mongod", "--replSet", "rs0", "--bind_ip_all");

    @DynamicPropertySource
    static void containersProperties(DynamicPropertyRegistry registry) {
        mongoDBContainer.start();
        registry.add("spring.data.mongodb.host", mongoDBContainer::getHost);
        registry.add("spring.data.mongodb.port", mongoDBContainer::getFirstMappedPort);
    }
}