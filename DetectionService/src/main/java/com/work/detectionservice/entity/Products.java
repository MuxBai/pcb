package com.work.detectionservice.entity;

import lombok.Data;

@Data
public class Products {
    private String serialNumber;
    private String frontImage;
    private String backImage;
    private String userId;
    private Integer defectLevel;
}
