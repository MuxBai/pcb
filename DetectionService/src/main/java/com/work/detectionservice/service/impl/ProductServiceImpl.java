package com.work.detectionservice.service.impl;

import com.alibaba.fastjson.JSON;
import com.work.commonconfig.config.filter.JwtFilter;
import com.work.commonconfig.dto.ProductMessage;
import com.work.commonconfig.exception.MyException;
import com.work.commonconfig.service.Publish;
import com.work.detectionservice.dao.ProductDao;
import com.work.detectionservice.dto.ImageUpdateDTO;
import com.work.detectionservice.dto.UploadProduct;
import com.work.detectionservice.entity.Products;
import com.work.detectionservice.mapper.ProductMapper;
import com.work.detectionservice.service.ProductService;
import com.work.detectionservice.utils.OssJavaSdk;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.springframework.data.redis.core.RedisTemplate;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.concurrent.TimeUnit;


@Service
public class ProductServiceImpl implements ProductService {
    private static final Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class.getName());

    @Autowired
    private ProductDao productDao;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private Publish publish;

    @Autowired
    private OssJavaSdk ossJavaSdk;

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;


    //传入照片数据
    @Override
    public Boolean insertProductData(UploadProduct uploadProduct, String token){
        if(!JwtFilter.isValidToken(token))
            MyException.throwError("token过期或无效",401);
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

    //修改缺陷等级
    @Override
    public Boolean updateDefectLevel(String serialNumber,Integer defectLevel){
        return productMapper.updateDefectLevel(serialNumber,defectLevel);
    }

    // 修改传入图片
    @Override
    public Boolean updateProductImages(ImageUpdateDTO dto, String token) {
        if(!JwtFilter.isValidToken(token))
            MyException.throwError("token过期或无效",401);
        String serialNumber = dto.getSerialNumber();
        if (serialNumber == null || serialNumber.isEmpty()) {
            logger.error("更新失败：序列号不能为空");
            return false;
        }

        MultipartFile frontFile = dto.getFrontImage();
        MultipartFile backFile = dto.getBackImage();

        if ((frontFile == null || frontFile.isEmpty()) && (backFile == null || backFile.isEmpty())) {
            logger.warn("未上传图片，跳过更新");
            return false;
        }

        // Redis 分布式锁 key：根据商品序列号加锁，避免同一时间多个用户修改同一商品图片
        String lockKey = "product:image:update:lock:" + serialNumber;
        RLock lock = redissonClient.getLock(lockKey);

        try {
            // 尝试加锁，最多等待3秒，获取到后自动释放锁时间为10秒
            if (lock.tryLock(3, 10, TimeUnit.SECONDS)) {

                // 已获取锁，开始执行更新逻辑
                Products products = new Products();
                products.setSerialNumber(serialNumber);

                if (frontFile != null && !frontFile.isEmpty()) {
                    // 上传正面图片到 OSS 并设置路径
                    String frontImagePath = ossJavaSdk.uploadOSS(frontFile, "front_" + serialNumber);
                    products.setFrontImage(frontImagePath);
                }

                if (backFile != null && !backFile.isEmpty()) {
                    // 上传背面图片到 OSS 并设置路径
                    String backImagePath = ossJavaSdk.uploadOSS(backFile, "back_" + serialNumber);
                    products.setBackImage(backImagePath);
                }

                // 更新数据库中的图片路径
                return productMapper.updateProductImages(products);

            } else {
                // 未获取到锁，说明有其他人正在修改该商品图片
                logger.warn("未能获取锁，可能正在被其他用户修改：{}", serialNumber);
                return false;
            }
        } catch (Exception e) {
            logger.error("更新图片失败：{}", e.getMessage());
            return false;
        } finally {
            // 最后释放锁，确保不会死锁
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
    // 根据工号查找他负责的产品
    @Override
    public List<Products> findProductByUserIdPaged(String userId, int pageNum, int pageSize) {
        // 生成 Redis 缓存 key
        String key = String.format("product:userId:%s:page:%d:size:%d", userId, pageNum, pageSize);

        // 尝试从缓存中获取
        Object cached = redisTemplate.opsForValue().get(key);
        if (cached != null) {
            String json = cached.toString();
            return JSON.parseArray(json, Products.class);
        }

        // 缓存未命中，查询数据库
        int offset = (pageNum - 1) * pageSize;
        List<Products> list = productMapper.findProductByUserId(userId, pageSize, offset);

        // 写入 Redis 缓存，设置 60 秒过期时间
        redisTemplate.opsForValue().set(key, JSON.toJSONString(list), 60, TimeUnit.SECONDS);

        return list;
    }

    // 获取相同人员负责的产品总数
    @Override
    public int countProductByUserId(String userId) {
        return productMapper.countProductByUserId(userId);
    }

    // 根据缺陷等级查找产品
    @Override
    public List<Products> findProductsByDefectLevelPaged(Integer defectLevel, int pageNum, int pageSize) {
        // Redis 缓存 key 格式
        String key = String.format("product:defectLevel:%d:page:%d:size:%d", defectLevel, pageNum, pageSize);

        // 查询缓存
        Object cached = redisTemplate.opsForValue().get(key);
        if (cached != null) {
            String json = cached.toString();
            return JSON.parseArray(json, Products.class);
        }

        // 缓存未命中，执行数据库查询
        int offset = (pageNum - 1) * pageSize;
        List<Products> list = productMapper.findProductsByDefectLevel(defectLevel, pageSize, offset);

        // 缓存结果，设置 60 秒过期
        redisTemplate.opsForValue().set(key, JSON.toJSONString(list), 60, TimeUnit.SECONDS);

        return list;
    }
    // 获取相同缺陷等级产品总数
    @Override
    public int countProductsByDefectLevel(Integer defectLevel) {
        return productMapper.countProductsByDefectLevel(defectLevel);
    }

    // 分页获取全部产品信息
    @Override
    public List<Products> findAllProductsPaged(int page, int pageSize) {
        // Redis 缓存 key 格式
        String key = String.format("product:all:page:%d:size:%d", page, pageSize);

        // 查询缓存
        Object cached = redisTemplate.opsForValue().get(key);
        if (cached != null) {
            String json = cached.toString();
            return JSON.parseArray(json, Products.class);
        }

        // 缓存未命中，查询数据库
        int offset = (page - 1) * pageSize;
        List<Products> list = productMapper.findAllProducts(pageSize, offset);

        // 写入缓存，设置 60 秒过期时间
        redisTemplate.opsForValue().set(key, JSON.toJSONString(list), 60, TimeUnit.SECONDS);

        return list;
    }

    // 统计全部产品总数
    @Override
    public int countAllProducts() {
        return productMapper.countAllProducts();
    }

    // 根据serial_number删除产品信息
    @Override
    public boolean deleteBySerialNumbers(List<String> serialNumbers, String token) {
        if (!JwtFilter.isValidToken(token))
            MyException.throwError("token过期或无效", 401);

        if (serialNumbers == null || serialNumbers.isEmpty()) {
            MyException.throwError("删除失败：序列号列表不能为空", 400);
        }

    /*Integer role = JwtFilter.getUserRoleFromToken(token);
    if (role != 2)
        MyException.throwError("权限不足", 403);*/

        // 统一锁 key，防止多个用户同时删除同一批产品
        String lockKey = "product:delete:serial:lock:" + serialNumbers.toString();
        RLock lock = redissonClient.getLock(lockKey);

        try {
            if (lock.tryLock(3, 10, TimeUnit.SECONDS)) {
                int result = productMapper.deleteBySerialNumber(serialNumbers);
                return result > 0;
            } else {
                MyException.throwError("产品信息正在被其他用户操作，请稍后重试", 429);
                return false;
            }
        } catch (Exception e) {
            MyException.throwError("删除产品信息失败：" + e.getMessage(), 500);
            return false;
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }


    // 根据user_id删除产品信息
    @Override
    public boolean deleteByUserIds(List<String> userIds, String token) {
        if (!JwtFilter.isValidToken(token))
            MyException.throwError("token过期或无效", 401);

        if (userIds == null || userIds.isEmpty()) {
            MyException.throwError("删除失败：用户ID列表不能为空", 400);
        }

    /*Integer role = JwtFilter.getUserRoleFromToken(token);
    if (role != 2)
        MyException.throwError("权限不足", 403);*/

        // 统一锁 key，避免并发删除同一批用户关联的产品
        String lockKey = "product:delete:user:lock:" + userIds.toString();
        RLock lock = redissonClient.getLock(lockKey);

        try {
            if (lock.tryLock(3, 10, TimeUnit.SECONDS)) {
                int result = productMapper.deleteByUserId(userIds);
                return result > 0;
            } else {
                MyException.throwError("产品信息正在被其他用户操作，请稍后重试", 429);
                return false;
            }
        } catch (Exception e) {
            MyException.throwError("删除产品信息失败：" + e.getMessage(), 500);
            return false;
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

}
