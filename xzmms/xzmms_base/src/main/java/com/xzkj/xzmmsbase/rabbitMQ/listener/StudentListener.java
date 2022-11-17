package com.xzkj.xzmmsbase.rabbitMQ.listener;

import com.google.protobuf.InvalidProtocolBufferException;
import com.rabbitmq.client.Channel;
import com.xzkj.base.handler.LmhException;
import com.xzkj.xzmmsbase.entity.TStudent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class StudentListener {
    @RabbitListener(queues = "student",containerFactory = "studentContainer")
    public void process(Message message, Channel channel){
        String consumerQueue = message.getMessageProperties().getConsumerQueue();
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        log.info("消费队列：{},消费id：{}",consumerQueue,deliveryTag);

        byte[] body = message.getBody();
        try {
            TStudent.Student student = TStudent.Student.parseFrom(body);
            log.info("消息内容 number:{}, name:{},sex:{},hobby:{},skill:{},duration:{}",
                    student.getNumber(),student.getName(),student.getSex(),student.getHobby(),student.getSkill(),student.getDuration());
            channel.basicAck(deliveryTag,false);
        } catch (InvalidProtocolBufferException e) {
            throw new LmhException("400","解析数据出错");
        } catch (IOException e) {
            throw new LmhException("400","消费者确认消息出错");
        }
    }
}
