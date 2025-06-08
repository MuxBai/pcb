package com.work.detectionservice.service;

import com.work.detectionservice.dto.ImageUpdateDTO;
import com.work.detectionservice.dto.UploadProduct;
import com.work.detectionservice.entity.Products;

import java.util.List;

public interface ProductService {
    // 传入照片数据
    public Boolean insertProductData(UploadProduct products, String token);

    // 修改缺陷等级
    public Boolean updateDefectLevel(String serialNumber,Integer defectLevel);

    // 更新照片
    Boolean updateProductImages(ImageUpdateDTO imageUpdateDTO, String token);

    // 根据用户id查找他负责的产品（分页展示，默认一页10条数据）
    List<Products> findProductByUserIdPaged(String userId, int pageSize, int offset);

    // 获取相同人员负责的产品总数
    int countProductByUserId(String userId);

    // 根据缺陷等级查找产品（分页展示，默认一页10条数据）
    List<Products> findProductsByDefectLevelPaged(Integer defectLevel, int page, int pageSize);

    // 获取相同缺陷等级产品总数
    int countProductsByDefectLevel(Integer defectLevel);

    // 分页获取全部产品信息
    List<Products> findAllProductsPaged(int page, int pageSize);

    // 统计全部产品总数
    int countAllProducts();


    // 根据serial_number删除产品信息
    boolean deleteBySerialNumbers(List<String> serialNumbers, String token);

    // 根据user_id删除产品信息
    boolean deleteByUserIds(List<String> userIds, String token);
}
