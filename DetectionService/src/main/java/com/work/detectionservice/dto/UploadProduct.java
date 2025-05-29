package com.work.detectionservice.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;


@Data
public class UploadProduct {
    private String serialNumber;
    private MultipartFile frontImage;
    private MultipartFile backImage;
    private String userId;
}
