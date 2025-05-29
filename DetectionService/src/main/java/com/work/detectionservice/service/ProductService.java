package com.work.detectionservice.service;

import com.work.detectionservice.dto.UploadProduct;
import com.work.detectionservice.entity.Products;

import java.util.List;

public interface ProductService {
    //传入照片数据
    public Boolean insertProductData(UploadProduct products);

    //修改缺陷等级
    public Boolean updateDefectLevel(String serialNumber,Integer defectLevel);

    //根据工号查找其负责产品的序列号
    public List<String> findSerialNumberByUserId(String userId);

    //根据序列号查找产品信息
    public Products findProductBySerialNumber(String serialNumber);
}
