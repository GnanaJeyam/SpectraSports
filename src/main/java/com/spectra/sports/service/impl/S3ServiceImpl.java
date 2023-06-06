package com.spectra.sports.service.impl;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.spectra.sports.response.SuccessResponse;
import com.spectra.sports.service.S3Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import static com.spectra.sports.constant.SuccessOrErrorMessages.IMAGE_UPLOADED_SUCCESSFULLY;

@Service
public class S3ServiceImpl implements S3Service {
    private static final String S3_PUBLIC_URL = "https://%s.s3.amazonaws.com/%s";
    private final String s3AccessKey;
    private final String s3SecretKey;
    private final String s3BucketName;

    public S3ServiceImpl(@Value("${spectra-sports.s3-access-key}") String s3AccessKey,
                         @Value("${spectra-sports.s3-secret-key}") String s3SecretKey,
                         @Value("${spectra-sports.s3-bucket-name}") String s3BucketName) {
        this.s3AccessKey = s3AccessKey;
        this.s3SecretKey = s3SecretKey;
        this.s3BucketName = s3BucketName;
    }

    public InputStream retrieveFileFromS3(String fileName) {
        var s3Client = this.getS3Client();
        var object = s3Client.getObject(this.s3BucketName, fileName);

        return object.getObjectContent().getDelegateStream();
    }

    public SuccessResponse uploadFileToS3(MultipartFile file) throws IOException {
        Assert.notNull(file, "Input File Cannot be null");
        var s3Client = this.getS3Client();
        var key = UUID.randomUUID() + file.getOriginalFilename().replaceAll("\\s+", "");
        s3Client.putObject(s3BucketName, key, file.getInputStream(), new ObjectMetadata());

        return SuccessResponse.defaultResponse(S3_PUBLIC_URL.formatted(s3BucketName, key), IMAGE_UPLOADED_SUCCESSFULLY);
    }

    private AmazonS3 getS3Client() {
        AWSCredentials credentials = new BasicAWSCredentials(this.s3AccessKey, this.s3SecretKey);
        AmazonS3 s3client = AmazonS3ClientBuilder
                .standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(Regions.AP_SOUTH_1)
                .build();

        return s3client;
    }
}
