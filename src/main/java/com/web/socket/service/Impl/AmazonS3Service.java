package com.web.socket.service.Impl;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AmazonS3Service {
    private S3Client s3Client;
    @Value("${aws.region}")
    private String regionName;
    @Value("${aws.bucket-name}")
    private String bucketName;
    @Value("${aws.access-key}")
    private String accessKey;
    @Value("${aws.secret-key}")
    private String secretKey;

    @PostConstruct
    private void initializeAmazon() {
        AwsCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);
        this.s3Client = S3Client
                .builder()
                .region(Region.of(regionName))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .build();
    }

    public String generateFileName(MultipartFile multiPart) {
        return new Date().getTime() + "-" + multiPart.getOriginalFilename().replace(" ", "_");
    }

    public String uploadImage(MultipartFile file, String path) throws IOException {
        s3Client.putObject(PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(path)
                        .contentType(file.getContentType())
                        .acl(ObjectCannedACL.PUBLIC_READ)
                        .build(),
                RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
        return String.format("https://%s.s3.%s.amazonaws.com/%s", bucketName, regionName, path);
    }

    public List<String> uploadMultipleImages(List<MultipartFile> files, String originalDirectory) throws IOException {
        return files.stream().parallel().map(file -> {
            try {
                String path = String.format("%s/%s", originalDirectory, UUID.randomUUID().toString());
                s3Client.putObject(PutObjectRequest.builder()
                                .bucket(bucketName)
                                .key(path)
                                .contentType(file.getContentType())
                                .acl(ObjectCannedACL.PUBLIC_READ)
                                .build(),
                RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
                return String.format("https://%s.s3.%s.amazonaws.com/%s", bucketName, regionName, path);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).toList();
    }
}
















































