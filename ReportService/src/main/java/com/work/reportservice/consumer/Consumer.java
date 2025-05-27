package com.work.reportservice.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.work.commonconfig.dto.ReportMessage;
import com.work.reportservice.entity.Reports;
import com.work.reportservice.service.ReportService;
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
    ObjectMapper objectMapper;

    @Autowired
    private ReportService reportService;

    @RabbitListener(queues = "${requestQueue}")
    public void receive(String message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag){

        try{
            ReportMessage reportMessage = objectMapper.readValue(message, ReportMessage.class);
            Reports reports = new Reports();

            //将reports需要的参数由reportMessage传入
            reports.setContent(reportMessage.getContent());
            reports.setCreatedAt(reportMessage.getCreatedAt());
            reports.setFrontDefectImg(reportMessage.getFrontDefectImg());
            reports.setBackDefectImg(reportMessage.getBackDefectImg());
            reports.setSerialNumber(reportMessage.getSerialNumber());

            //调用service层方法
            reportService.insertReport(reports);

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
