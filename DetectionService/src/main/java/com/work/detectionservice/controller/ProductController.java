package com.work.detectionservice.controller;

import com.work.commonconfig.config.filter.JwtFilter;
import com.work.commonconfig.exception.MyException;
import com.work.detectionservice.dto.ImageUpdateDTO;
import com.work.detectionservice.dto.UploadProduct;
import com.work.detectionservice.entity.Products;
import com.work.detectionservice.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/detection")
public class ProductController {
 /*   @Autowired
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
    }*/
 @Autowired
 private ProductService productService;

    // 根据工号分页查询负责的产品
    @GetMapping("/by-user")
    public ResponseEntity<?> getProductsByUserPaged(
            @RequestParam String userId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        try {
            List<Products> products = productService.findProductByUserIdPaged(userId, page, pageSize);
            int total = productService.countProductByUserId(userId);

            Map<String, Object> result = new HashMap<>();
            result.put("data", products);
            result.put("total", total);
            result.put("page", page);
            result.put("size", pageSize);

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("服务器内部错误");
        }
    }

    // 根据缺陷等级分页查询产品
    @GetMapping("/by-defectlevel")
    public ResponseEntity<?> getProductsByDefectLevelPaged(
            @RequestParam Integer defectLevel,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        try {
            List<Products> products = productService.findProductsByDefectLevelPaged(defectLevel, page, pageSize);
            int total = productService.countProductsByDefectLevel(defectLevel);

            Map<String, Object> result = new HashMap<>();
            result.put("data", products);
            result.put("total", total);
            result.put("page", page);
            result.put("size", pageSize);

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("服务器内部错误");
        }
    }

    // 分页查询所有产品
    @GetMapping("/all")
    public ResponseEntity<?> getAllProductsPaged(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        try {
            List<Products> products = productService.findAllProductsPaged(page, pageSize);
            int total = productService.countAllProducts();

            Map<String, Object> result = new HashMap<>();
            result.put("data", products);
            result.put("total", total);
            result.put("page", page);
            result.put("size", pageSize);

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("服务器内部错误");
        }
    }

    // 修改传入的图片
    @PostMapping("/update-image")
    public ResponseEntity<?> updateProductImages(
            @RequestPart("serialNumber") String serialNumber,
            @RequestPart(value = "frontImage", required = false) MultipartFile frontImage,
            @RequestPart(value = "backImage", required = false) MultipartFile backImage,
            @RequestHeader("Authorization") String token) {

        ImageUpdateDTO dto = new ImageUpdateDTO();
        dto.setSerialNumber(serialNumber);
        dto.setFrontImage(frontImage);
        dto.setBackImage(backImage);

        try {
            boolean updated = productService.updateProductImages(dto, token);
            if (updated) {
                return ResponseEntity.ok("图片更新成功");
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("更新失败，可能未上传图片或序列号无效");
            }
        } catch (MyException e) {
            return ResponseEntity.status(e.getCode()).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("服务器内部错误");
        }
    }

    // 传入图片
    @PostMapping("/insert-image")
    public ResponseEntity<?> insertProductData(
            @RequestPart("userId") String userId,
            @RequestPart(" ") MultipartFile frontImage,
            @RequestPart("backImage") MultipartFile backImage,
            @RequestHeader("Authorization") String token) {

        try {
            // 封装上传数据
            UploadProduct uploadProduct = new UploadProduct();
            uploadProduct.setUserId(userId);
            uploadProduct.setFrontImage(frontImage);
            uploadProduct.setBackImage(backImage);

            // 调用 Service 插入方法
            boolean inserted = productService.insertProductData(uploadProduct, token);

            if (inserted) {
                return ResponseEntity.ok("图片上传成功");
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("上传失败，图片可能为空或其他异常");
            }

        } catch (MyException e) {
            return ResponseEntity.status(e.getCode()).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("服务器内部错误");
        }
    }

    @PostMapping("/update-defectLevel")
    public ResponseEntity<?> updateDefectLevel(
            @RequestParam("serialNumber") String serialNumber,
            @RequestParam("defectLevel") Integer defectLevel,
            @RequestHeader("Authorization") String token) {

        try {
            // 验证 Token
            if (!JwtFilter.isValidToken(token)) {
                MyException.throwError("token过期或无效", 401);
            }

            boolean updated = productService.updateDefectLevel(serialNumber, defectLevel);
            if (updated) {
                return ResponseEntity.ok("缺陷等级更新成功");
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("更新失败，序列号无效或数据库错误");
            }

        } catch (MyException e) {
            return ResponseEntity.status(e.getCode()).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("服务器内部错误");
        }
    }

    // 根据 serial_number 删除产品信息
    @DeleteMapping("/by-serial")
    public ResponseEntity<String> deleteBySerialNumbers(@RequestBody List<String> serialNumbers,
                                                        @RequestHeader("Authorization") String token) {
        try {
            boolean deleted = productService.deleteBySerialNumbers(serialNumbers, token);
            return deleted ? ResponseEntity.ok("删除成功")
                    : ResponseEntity.status(404).body("未找到对应产品信息或删除失败");
        } catch (MyException e) {
            return ResponseEntity.status(e.getCode()).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("服务器内部错误：" + e.getMessage());
        }
    }

    // 根据 user_id 删除产品信息
    @DeleteMapping("/by-user")
    public ResponseEntity<String> deleteByUserIds(@RequestBody List<String> userIds,
                                                  @RequestHeader("Authorization") String token) {
        try {
            boolean deleted = productService.deleteByUserIds(userIds, token);
            return deleted ? ResponseEntity.ok("删除成功")
                    : ResponseEntity.status(404).body("未找到对应产品信息或删除失败");
        } catch (MyException e) {
            return ResponseEntity.status(e.getCode()).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("服务器内部错误：" + e.getMessage());
        }
    }
}
