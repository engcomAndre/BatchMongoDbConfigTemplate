package com.test;


import org.bson.Document;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.event.annotation.BeforeTestClass;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ContextConfiguration(classes = ApplicationBatchCronJob.class)
class ApplicationBatchCronJobTest extends MongoDBContainerConfig {

    @Autowired
    private MongoTemplate mongoTemplate;

    private final List<String> collections = List.of(
        "BATCH_JOB_EXECUTION",
        "BATCH_JOB_INSTANCE",
        "BATCH_STEP_EXECUTION",
        "BATCH_SEQUENCES"
    );

    private final List<String> sequences = List.of(
        "BATCH_STEP_EXECUTION_SEQ",
        "BATCH_JOB_INSTANCE_SEQ",
        "BATCH_JOB_EXECUTION_SEQ"
    );

    @BeforeTestClass
    public void setUp() {
        collections.forEach(collection -> {
            if (mongoTemplate.collectionExists(collection)) {
                mongoTemplate.dropCollection(collection);
            }
        });
    }

    @Test
    public void should_init_batch_metadata_db_structure() {
        assertThat(collections).allMatch(collection -> mongoTemplate.collectionExists(collection));

        final var savedSequences = mongoTemplate.getCollection("BATCH_SEQUENCES").find()
            .projection(new Document("_id", 1))
            .into(new java.util.ArrayList<>())
            .stream()
            .map(doc -> doc.getString("_id"))
            .toList();

        assertThat(sequences).allMatch(savedSequences::contains);
    }

    @Test
    public void should_metadata_table_not_empty() {
        collections.forEach(
            collection -> assertThat(mongoTemplate.getCollection(collection).countDocuments()).isGreaterThan(0)
        );
    }

    @Test
    public void sequences_should_be_incremented() {
        sequences.forEach(sequence -> {
            var document = mongoTemplate.getCollection("BATCH_SEQUENCES").find(new Document("_id", sequence)).first();
            assertThat(document).isNotNull();
            assertThat(document.getLong("count")).isGreaterThan(0L);
        });
    }

}