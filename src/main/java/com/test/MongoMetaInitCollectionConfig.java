package com.test;


import jakarta.annotation.PostConstruct;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.List;

@Configuration
public class MongoMetaInitCollectionConfig {

    private final List<String> sequences = List.of(
        "BATCH_STEP_EXECUTION_SEQ",
        "BATCH_JOB_INSTANCE_SEQ",
        "BATCH_JOB_EXECUTION_SEQ"
    );

    @Autowired
    private MongoTemplate mongoTemplate;

    @PostConstruct
    public void init() {
        if (!mongoTemplate.collectionExists("BATCH_SEQUENCES")) {
            mongoTemplate.createCollection("BATCH_SEQUENCES");
        }

        List<String> ids = mongoTemplate.getCollection("BATCH_SEQUENCES").find()
            .projection(new Document("_id", 1))
            .into(new java.util.ArrayList<>())
            .stream()
            .map(doc -> doc.getString("_id"))
            .toList();

        sequences.forEach(sequence -> {
                if (!ids.contains(sequence)) {
                    Document document = new Document();
                    document.put("_id", sequence);
                    document.put("count", 0L);
                    mongoTemplate.getCollection("BATCH_SEQUENCES").insertOne(document);
                }
            });
    }
}
