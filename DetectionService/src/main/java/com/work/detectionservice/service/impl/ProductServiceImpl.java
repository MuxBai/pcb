package com.work.detectionservice.service.impl;

import com.work.detectionservice.dao.ProductDao;
import com.work.detectionservice.entity.Products;
import com.work.detectionservice.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {
    @Autowired
    private ProductDao productDao;

    @Override
    public Boolean insertProductData(Products products){
        Integer num = productDao.countProduct()+1;
        String id = num.toString();
        if(id.length()<6){
            String first = "PCB_Board";
            for(int i = 0;i < 6-id.length();i++){
                first+="0";
            }
            id = first+id;
        }
        products.setSerialNumber(id);
        return productDao.insertProductData(products);
    }

    @Override
    public Boolean updateDefectLevel(String serialNumber,Integer defectLevel){
        return productDao.updateDefectLevel(serialNumber,defectLevel);
    }

    @Override
    public List<String> findSerialNumberByUserId(String userId){
        return productDao.findSerialNumberByUserId(userId);
    }

    @Override
    public Products findProductBySerialNumber(String serialNumber){
        return productDao.findProductBySerialNumber(serialNumber);
    }
}
