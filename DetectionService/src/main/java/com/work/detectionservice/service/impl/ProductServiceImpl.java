package com.work.detectionservice.service.impl;

import com.work.commonconfig.dto.ProductMessage;
import com.work.commonconfig.service.Publish;
import com.work.detectionservice.dao.ProductDao;
import com.work.detectionservice.entity.Products;
import com.work.detectionservice.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {
    private static final Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class.getName());

    @Autowired
    private ProductDao productDao;

    @Autowired
    private Publish publish;

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

        //创建一个传入消息队列用到的中间类
        //传入参数包含PCB板的ID，正面图片，反面图片
        ProductMessage message = new ProductMessage();
        message.setSerialNumber(id);
        message.setFrontImage(products.getFrontImage());
        message.setBackImage(products.getBackImage());
        //调用生产者方法将数据送入队列中
        try{
            publish.sendMessage(message);
        }catch (Exception e){
            logger.error("向队列消息发送失败: {}", e.getMessage());
            e.printStackTrace();
        }
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
