package com.daruda.darudaserver.global.infra.S3;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.SystemPropertyCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
public class AWSConfig {

    private static final String AWS_ACCESS_KEY_ID = "aws.accessKeyId";
    private static final String AWS_SECRET_ACCESS_KEY = "aws.secretAccessKey";
    private final String accessKey;
    private final String secretKey;
    private final String regionString;

    public AWSConfig(@Value("${cloud.aws.credentials.access-key}") final String accessKey,
                     @Value("${cloud.aws.credentials.secret-key}") final String secretKey,
                     @Value("${cloud.aws.region.static}") final String regionString){
        this.accessKey = accessKey;
        this.secretKey = secretKey;
        this.regionString = regionString;
    }

    //자격 증명 공급자 생성
    @Bean
    public SystemPropertyCredentialsProvider systemPropertyCredentialsProvider(){
        System.setProperty(AWS_ACCESS_KEY_ID, accessKey);
        System.setProperty(AWS_SECRET_ACCESS_KEY,secretKey);
        return SystemPropertyCredentialsProvider.create();
    }

    @Bean
    public Region getRegion(){
        return Region.of(regionString);
    }

    @Bean
    public S3Client getS3Client(){
        return S3Client.builder()
                .region(getRegion())
                .credentialsProvider(systemPropertyCredentialsProvider())
                .build();
    }
}
