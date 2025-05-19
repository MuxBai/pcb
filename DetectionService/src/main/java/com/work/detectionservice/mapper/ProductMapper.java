package com.work.detectionservice.mapper;

import com.work.detectionservice.entity.Products;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ProductMapper {

    //传入照片数据
    public Boolean insertProductData(Products products);

    //修改缺陷等级
    public Boolean updateDefectLevel(String serialNumber,Integer defectLevel);

    //根据工号查找其负责产品的序列号
    public List<String> findSerialNumberByUserId(String userId);

    //根据序列号查找产品信息
    public Products findProductBySerialNumber(String serialNumber);

    //统计现有记录产品数量
    public Integer countProduct();
}
