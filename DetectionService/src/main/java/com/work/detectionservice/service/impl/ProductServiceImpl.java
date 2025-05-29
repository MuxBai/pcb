package com.work.detectionservice.service.impl;

import com.work.commonconfig.dto.ProductMessage;
import com.work.commonconfig.service.Publish;
import com.work.detectionservice.dao.ProductDao;
import com.work.detectionservice.dto.UploadProduct;
import com.work.detectionservice.entity.Products;
import com.work.detectionservice.service.ProductService;
import com.work.detectionservice.utils.OssJavaSdk;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {
    private static final Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class.getName());

    @Autowired
    private ProductDao productDao;

    @Autowired
    private Publish publish;

    @Autowired
    private OssJavaSdk ossJavaSdk;

    @Override
    public Boolean insertProductData(UploadProduct uploadProduct){
        Integer num = productDao.countProduct()+1;
        String id = num.toString();
        if(id.length()<6){
            String first = "PCB_Board";
            for(int i = 0;i < 6-id.length();i++){
                first+="0";
            }
            id = first+id;
        }
        try{

            uploadProduct.setSerialNumber(id);

            //获取图片并判断图片是否为空
            MultipartFile frontFile = uploadProduct.getFrontImage();
            MultipartFile backFile = uploadProduct.getBackImage();
            if (frontFile == null || frontFile.isEmpty() || backFile == null || backFile.isEmpty()) {
                logger.error("上传失败：前后图片不能为空");
                return false;
            }

            //上传图片并返回得到一个存放路径
            String frontImagePath = ossJavaSdk.uploadOSS(uploadProduct.getFrontImage(),"front_"+id);
            String backImagePath = ossJavaSdk.uploadOSS(uploadProduct.getBackImage(),"backend_"+id);

            //创建一个传入消息队列用到的中间类
            //传入参数包含PCB板的ID，正面图片，反面图片
            ProductMessage message = new ProductMessage();
            message.setSerialNumber(id);
            message.setFrontImage(frontImagePath);
            message.setBackImage(backImagePath);
            //调用生产者方法将数据送入队列中
            publish.sendMessage(message);

            //创建需要插入的数据
            Products products = new Products();
            products.setSerialNumber(id);
            products.setFrontImage(frontImagePath);
            products.setBackImage(backImagePath);
            products.setUserId(uploadProduct.getUserId());
            products.setDefectLevel(0);
            return productDao.insertProductData(products);
        }catch (Exception e){
            logger.error("向队列消息发送失败: {}", e.getMessage());
            e.printStackTrace();
        }
        return false;
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
