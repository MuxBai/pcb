package com.work.detectionservice.dao;

import com.work.detectionservice.entity.Products;

import java.util.List;

public interface ProductDao {
    public Boolean insertProductData(Products products);
    public Boolean updateDefectLevel(String serialNumber,Integer defectLevel);
    public List<String> findSerialNumberByUserId(String userId);
    public Products findProductBySerialNumber(String serialNumber);
    public Integer countProduct();
}
