package com.work.detectionservice.utils;

import java.io.*;
import java.time.LocalDate;

import com.aliyun.oss.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class OssJavaSdk {
    /** 生成一个唯一的 Bucket 名称 */

    @Autowired
    private OSS ossClient;

    public String uploadOSS(MultipartFile file, String serialNumber) throws com.aliyuncs.exceptions.ClientException {
        // Endpoint以华东1（杭州）为例，填写为https://oss-cn-hangzhou.aliyuncs.com，其它Region请按实际情况填写。
        String bucketName = "detection-pcb-test";
        // 填写Bucket所在地域。以华东1（杭州）为例，Region填写为cn-hangzhou。
        // 关于OSS支持的Region与Endpoint的对应关系，请参见https://www.alibabacloud.com/help/zh/oss/user-guide/regions-and-endpoints。
        String region = "cn-chengdu";
        try {
            // 获取当前日期，并拼接为路径结构
            LocalDate today = LocalDate.now();
            String datePath = today.getYear() + "/" + today.getMonthValue() + "/" + today.getDayOfMonth();

            // 获取原始文件扩展名（.jpg, .png等）
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }

            // 2. 上传文件
            String objectName = "java_upload/" + datePath + "/" + serialNumber + extension;
//            String content = "Hello OSS";
            ossClient.putObject(bucketName, objectName, new ByteArrayInputStream(file.getBytes()));
            System.out.println("文件 " + objectName + " 上传成功。");
            return "https://" + bucketName + ".oss-" + region + ".aliyuncs.com/" + objectName;
        } catch (OSSException oe) {
            System.out.println("Caught an OSSException, which means your request made it to OSS, "
                    + "but was rejected with an error response for some reason.");
            System.out.println("Error Message:" + oe.getErrorMessage());
            System.out.println("Error Code:" + oe.getErrorCode());
            System.out.println("Request ID:" + oe.getRequestId());
            System.out.println("Host ID:" + oe.getHostId());
        } catch (ClientException ce) {
            System.out.println("Caught an ClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with OSS, "
                    + "such as not being able to access the network.");
            System.out.println("Error Message:" + ce.getMessage());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return "OSS上传失败";
    }
}