package com.work.detectionservice.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.work.commonconfig.dto.ReportMessage;
import com.work.detectionservice.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class Consumer {
    private static final Logger logger = LoggerFactory.getLogger(Consumer.class.getName());

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProductService productService;

    @RabbitListener(queues = "${resultQueueTwo}")
    public void receive(String message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag){
        System.out.println("拿到数据");
        try{
            ReportMessage reportMessage = objectMapper.readValue(message, ReportMessage.class);
            System.out.println(reportMessage);
            //对拿到的数据反序列化后取出PCB板的ID和缺陷等级并更新
            if(reportMessage.getDefectLevel() != null) {
                productService.updateDefectLevel(reportMessage.getSerialNumber(), reportMessage.getDefectLevel());
                System.out.println("已经完成缺陷等级更新");
            }
            channel.basicAck(deliveryTag, false);
        }catch (Exception e){
            logger.error("接收队列数据错误: {}", e.getMessage());
            try{
                channel.basicNack(deliveryTag, false, true);
            }catch (IOException ioe){
                logger.error("重新放入队列数据错误: {}", ioe.getMessage());
            }
        }
    }
}
