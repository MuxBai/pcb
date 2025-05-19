package com.work.detectionservice.dao.impl;

import com.work.detectionservice.dao.ProductDao;
import com.work.detectionservice.entity.Products;
import com.work.detectionservice.mapper.ProductMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ProductDaoImpl implements ProductDao {
    @Autowired
    private ProductMapper productMapper;

    @Override
    public Boolean insertProductData(Products products){
        return productMapper.insertProductData(products);
    }

    @Override
    public Boolean updateDefectLevel(String serialNumber,Integer defectLevel){
        return productMapper.updateDefectLevel(serialNumber,defectLevel);
    }

    @Override
    public List<String> findSerialNumberByUserId(String userId){
        return productMapper.findSerialNumberByUserId(userId);
    }

    @Override
    public Products findProductBySerialNumber(String serialNumber){
        return productMapper.findProductBySerialNumber(serialNumber);
    }

    @Override
    public Integer countProduct(){
        return productMapper.countProduct();
    }
}
