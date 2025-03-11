package com.test;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

import java.util.UUID;

@SpringBootApplication
public class ApplicationBatchCronJob implements CommandLineRunner {

    @Autowired
    JobLauncher jobLauncher;

    @Autowired
    Job job;

    public static void main(String[] args) {
        new SpringApplicationBuilder(ApplicationBatchCronJob.class)
            .web(WebApplicationType.NONE)
            .run(args);
    }

    @Override
    public void run(String... args) throws Exception {
        JobParameters jobParameters = new JobParametersBuilder()
            .addString("run_id", UUID.randomUUID().toString()) // Use underline (_) no lugar de ponto (.)
            .toJobParameters();
        jobLauncher.run(job, jobParameters);

    }
}
