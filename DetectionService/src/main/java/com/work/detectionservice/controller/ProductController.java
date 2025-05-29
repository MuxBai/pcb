package com.work.detectionservice.controller;

import com.work.detectionservice.dto.UploadProduct;
import com.work.detectionservice.entity.Products;
import com.work.detectionservice.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/detection")
public class ProductController {
    @Autowired
    private ProductService productService;

    @PostMapping("/insert")
    public Boolean insertProductData(@ModelAttribute UploadProduct products) {
        return productService.insertProductData(products);
    }

    @PostMapping("/update")
    public Boolean updateDefectLevel(@RequestBody Map<String,String> request) {
        return productService.updateDefectLevel(request.get("serialNumber"),Integer.valueOf(request.get("defectLevel")));
    }

    @GetMapping("/findProduct/{serialNumber}")
    public Products findProductBySerialNumber(@PathVariable String serialNumber) {
        return productService.findProductBySerialNumber(serialNumber);
    }

    @GetMapping("/findByUserId/{userId}")
    public List<String> findSerialNumberByUserId(@PathVariable String userId) {
        return productService.findSerialNumberByUserId(userId);
    }
}
