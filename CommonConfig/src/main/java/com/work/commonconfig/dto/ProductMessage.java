package com.work.commonconfig.dto;

import lombok.Data;

//传入消息队列的中间类
//仅包含PCB板的ID，待识别的正面图片和反面图片

@Data
public class ProductMessage {
    private String serialNumber;
    private String frontImage;
    private String backImage;
}
