package com.openrangelabs.services.ticket.storage;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.xml.bind.DatatypeConverter;
import java.io.InputStream;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class S3Service {

    AWSCredentials credentials;
    AmazonS3 s3Client;
    String TICKET_ATTACHMENT_BUCKET = "Ticket_Attachments";
    String DATACENTER_DOCUMENTS = "Datacenter_Documents";
    @Value("${swiftStackAccessKey}")
    String swiftStackAccessKey;
    @Value("${swiftStackSecreteKey}")
    String swiftStackSecreteKey;
    @Value("${swiftStackURL}")
    String swiftStackURL;

    String connectionError = "Could not connect to store file";
    String attachmentError = "ATTACHMENT FAILURE: bucket=";

    private void buildS3Client() {
        credentials = new BasicAWSCredentials(swiftStackAccessKey, swiftStackSecreteKey);
        log.info("S3 CONNECTION: url="+swiftStackURL+" bucket="+TICKET_ATTACHMENT_BUCKET+" accessKey="+swiftStackAccessKey+" secretKet="+swiftStackSecreteKey+" region=us-east-1");
        s3Client = AmazonS3ClientBuilder.standard()
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(swiftStackURL, "us-east-1"))
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .build();
    }

    public S3Service buildS3DatacenterClient() {
        credentials = new BasicAWSCredentials(swiftStackAccessKey, swiftStackSecreteKey);
        log.info("S3 CONNECTION: url="+swiftStackURL+" bucket="+DATACENTER_DOCUMENTS+" accessKey="+swiftStackAccessKey+" secretKet="+swiftStackSecreteKey+" region=us-east-1");
        s3Client = AmazonS3ClientBuilder.standard()
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(swiftStackURL, "us-east-1"))
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .build();
        return this;
    }

    public String saveFile(String userName, InputStream upload, String contentType, Long size , String container) throws Exception {
        if(null != s3Client  && container.equals(TICKET_ATTACHMENT_BUCKET)) {
            buildS3Client();
        }else if(s3Client == null && container.equals(DATACENTER_DOCUMENTS)){
            buildS3DatacenterClient();
        }
        List<Bucket> bucketList = null;
        if(null != s3Client  && null != s3Client.listBuckets()) {
            bucketList = s3Client.listBuckets();
        }
        log.info("S3 Buckets: "+bucketList);

        if (null != bucketList && bucketList.size() < 1) {
            log.info(attachmentError+container);
            throw new Exception (connectionError);
        }
        ObjectListing objectList = null;
        if(null != s3Client){
            objectList = s3Client.listObjects(container);
        }
        if (null == objectList) {
            log.info(attachmentError+container);
            throw new Exception (connectionError);
        } else {
            for (S3ObjectSummary objectSummary : objectList.getObjectSummaries()) {
                log.info("S3 Bucket Object: %s (size: %d) OWNER: %s\n", objectSummary.getKey(), objectSummary.getSize(), objectSummary.getOwner());
            }
        }

        String myKey = userName+LocalDateTime.now().toString();
        MessageDigest md = MessageDigest.getInstance("SHA-512");
        md.update(myKey.getBytes());
        byte[] digest = md.digest();
        String key = DatatypeConverter.printHexBinary(digest).toUpperCase();
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(contentType);
        objectMetadata.setHeader("username", userName);
        objectMetadata.setHeader("sourceType", "ticketAttachment");
        objectMetadata.setContentLength(size);
        log.info("ATTACHMENT: bucket="+container+" key  ="+key+" size="+size);
        try {
            PutObjectResult result = s3Client.putObject(container, key, upload, objectMetadata);
            log.info("ATTACHMENT: bucket="+container+" key= "+key+" version="+result.getVersionId()+" md5="+result.getContentMd5()+" etag="+result.getETag()+" expire="+result.getExpirationTime());
        } catch (Exception e) {
            log.info(attachmentError+container+" key ="+key+" size="+size+" message="+e.getMessage());
            throw new Exception (connectionError);
        }
        return key;
    }

    public S3Object getFile(String key , String container) {
        if(s3Client == null && container.equals(TICKET_ATTACHMENT_BUCKET)) {
            buildS3Client();
        }else if(s3Client == null && container.equals(DATACENTER_DOCUMENTS)){
            buildS3DatacenterClient();
        }
        if(null != s3Client && null != s3Client.getObject(container, key)) {
            S3Object result = s3Client.getObject(container, key);

            return result;
        }
        return new S3Object();
    }

    public InputStream getFileStream(String key, String container) {
        if(s3Client == null && container.equals(TICKET_ATTACHMENT_BUCKET)) {
            buildS3Client();
        }else if(s3Client == null && container.equals(DATACENTER_DOCUMENTS)){
            buildS3DatacenterClient();
        }
        if(null != s3Client && null != s3Client.getObject(container, key)) {
            S3Object result = s3Client.getObject(container, key);

            return result.getObjectContent();
        }
        return null;
    }
}
