package com.work.detectionservice.mapper;

import com.work.detectionservice.entity.Products;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ProductMapper {

    // 传入照片数据
    public Boolean insertProductData(Products products);

    // 修改缺陷等级
    public Boolean updateDefectLevel(String serialNumber,Integer defectLevel);

    // 根据工号查找其负责产品信息
    public List<Products> findProductByUserId(String userId, int pageSize, int offset);
    // 统计同一人员负责产品的总数
    int countProductByUserId(String userId);

    // 统计现有记录产品数量
    public Integer countProduct();

    // 修改照片数据
    Boolean updateProductImages(Products products);

    // 根据缺陷等级查找产品
    List<Products> findProductsByDefectLevel(Integer defectLevel, int pageSize, int offset);
    // 统计相同缺陷等级产品的总数
    int countProductsByDefectLevel(Integer defectLevel);

    // 分页获取产品信息
    List<Products> findAllProducts(int pageSize, int offset);
    //统计全部产品总数
    int countAllProducts();

    int deleteBySerialNumber(List<String> serialNumbers);
    int deleteByUserId(List<String> userIds);

}
