package com.web.socket.config;

import com.web.socket.entity.User;
import com.web.socket.repository.UserRepository;
import com.web.socket.utils.SecurityUtils;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
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

@Service
@RequiredArgsConstructor
public class AmazonClient {
    private S3Client s3Client;
    private final UserRepository userRepository;

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
    public void uploadFileToS3(MultipartFile file) throws IOException {
        Authentication authentication = SecurityUtils.getAuthentication();
        String username = ((UserDetails) authentication.getPrincipal()).getUsername();
        User authenticatedUser = userRepository.findByUsername(username).orElseThrow(() -> new BadCredentialsException("Invalid credential"));

        String path = String.format("user-avt/%s", authenticatedUser.getId());
        s3Client.putObject(PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(path)
                        .contentType("image/png")
                        .acl(ObjectCannedACL.PUBLIC_READ)
                        .build(),
                RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

        String avtUrl = String.format("https://%s.s3.%s.amazonaws.com/%s", bucketName, regionName, path);
        authenticatedUser.setAvatar(avtUrl);
        userRepository.save(authenticatedUser);
    }
}
