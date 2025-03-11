package com.test;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.launch.support.TaskExecutorJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.MongoJobRepositoryFactoryBean;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.DbRefResolver;
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.List;

@Service
public class BatchLauncher {

    @Bean
    public JobLauncher jobLauncher(JobRepository jobRepository) throws Exception {
        var jobLauncher = new TaskExecutorJobLauncher();
        jobLauncher.setJobRepository(jobRepository);
        jobLauncher.afterPropertiesSet();
        return jobLauncher;
    }

    @Bean
    public MappingMongoConverter mongoConverter(MongoDatabaseFactory mongoFactory, MongoMappingContext mongoMappingContext) throws Exception {
        DbRefResolver dbRefResolver = new DefaultDbRefResolver(mongoFactory);
        MappingMongoConverter mongoConverter = new MappingMongoConverter(dbRefResolver, mongoMappingContext);
        mongoConverter.setMapKeyDotReplacement(".");
        return mongoConverter;
    }

    @Bean("mongoTransactionManager")
    @Qualifier("mongoTransactionManager")
    public MongoTransactionManager transactionManager(MongoDatabaseFactory mongoDatabaseFactory) {
        MongoTransactionManager mongoTransactionManager = new MongoTransactionManager();
        mongoTransactionManager.setDbFactory(mongoDatabaseFactory);
        mongoTransactionManager.afterPropertiesSet();
        return mongoTransactionManager;
    }

    @Bean("mongoJobRepository")
    @Qualifier("mongoJobRepository")
    public JobRepository jobRepository(MongoTemplate mongoTemplate, MongoTransactionManager transactionManager)
        throws Exception {
        MongoJobRepositoryFactoryBean jobRepositoryFactoryBean = new MongoJobRepositoryFactoryBean();
        jobRepositoryFactoryBean.setMongoOperations(mongoTemplate);
        jobRepositoryFactoryBean.setTransactionManager(transactionManager);
        jobRepositoryFactoryBean.afterPropertiesSet();
        return jobRepositoryFactoryBean.getObject();
    }

    @Bean
    public Job BatchProcessJob(
        @Qualifier("mongoJobRepository") JobRepository jobRepository,
        Step batchProcessCloseStep
    ) {
        return new JobBuilder("BatchProcessJob", jobRepository)
            .incrementer(new RunIdIncrementer())
            .start(batchProcessCloseStep)
            .build();
    }

    @Bean
    public Step batchProcessCloseStep(
        @Qualifier("mongoJobRepository") JobRepository jobRepository,
        @Qualifier("mongoTransactionManager") PlatformTransactionManager transactionManager,
        ItemReader<String> batchProcesssItemReader,
        ItemProcessor<String, String> batchProcessProcessor,
        ItemWriter<String> batchProcesssWriter
    ) {
        return new StepBuilder("batchProcessCloseStep", jobRepository)
            .<String, String>chunk(10, transactionManager)
            .reader(batchProcesssItemReader)
            .processor(batchProcessProcessor)
            .writer(batchProcesssWriter)
            .allowStartIfComplete(true)
            .build();
    }

    public List<String> getItems() {
        return List.of(
            "item1",
            "item2",
            "item3",
            "item4",
            "item5",
            "item6",
            "item7",
            "item8",
            "item9",
            "item10"
        );
    }

    @Bean
    public ItemReader<String> batchProcesssItemReader() {
        return new ListItemReader<>(getItems());
    }

    @Bean
    public ItemProcessor<String, String> invoiceProcessor() {
        return String::toUpperCase;
    }

    @Bean
    public ItemWriter<String> batchProcesssWriter() {
        return inv -> inv.getItems().forEach(System.out::println);
    }
}
