//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.spectra.sports.service;

import com.spectra.sports.response.SuccessResponse;
import java.io.IOException;
import java.io.InputStream;
import org.springframework.web.multipart.MultipartFile;

public interface S3Service {
    InputStream retrieveFileFromS3(String fileName);

    SuccessResponse<String> uploadFileToS3(MultipartFile inputStream) throws IOException;
}
