package com.openrangelabs.services.release.storage;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.retry.PredefinedRetryPolicies;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.S3Object;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.InputStream;

@Service
public class S3ReleaseService {

    AWSCredentials credentials;
    AmazonS3 s3Client;
    String Release_PDFs = "Release_PDFs";

    @Value("${swiftStackAccessKey}")
    String swiftStackAccessKey;
    @Value("${swiftStackSecreteKey}")
    String swiftStackSecreteKey;
    @Value("${swiftStackURL}")
    String swiftStackURL;

    private void buildS3Client() {
        credentials = new BasicAWSCredentials(swiftStackAccessKey, swiftStackSecreteKey);
        s3Client = AmazonS3ClientBuilder.standard()
                .withClientConfiguration(new ClientConfiguration()
                        .withMaxConnections(200)
                        .withConnectionTimeout(7500)
                        .withSocketTimeout(7500)
                        .withRetryPolicy(PredefinedRetryPolicies.getDefaultRetryPolicyWithCustomMaxRetries(3))
                )
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(swiftStackURL, "us-east-1"))
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .build();
    }

    public InputStream getFile(String key) {
        if(s3Client == null) {
            buildS3Client();
        }
        S3Object result = s3Client.getObject(Release_PDFs, key);
        return result.getObjectContent();
    }
}
