package com.work.commonconfig.dto;

import lombok.Data;

import java.time.LocalDateTime;

//这个类用于返回Reports类需要的字段
//以及由于缺陷等级在DetectionService的实体类中
//不能直接返回给ReportService，所以需要一个中间类
//额外携带defectLevel字段并在DetectionService中也存在一个消费者专门获取缺陷等级

@Data
public class ReportMessage {
    private String content;
    private LocalDateTime createdAt;
    private String frontDefectImg;
    private String backDefectImg;
    private String serialNumber;
    private Integer defectLevel;
}
