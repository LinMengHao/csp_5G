package com.xzkj.ossService.rabbitMQ;

import com.rabbitmq.client.*;
import com.xzkj.base.handler.LmhException;
import com.xzkj.ossService.entity.TStudent;


public class RabbitSendUtils {
    public static void publishStudent(TStudent.Student student) {
        ConnectionFactory factory=new ConnectionFactory();
        Connection connection=null;
        try {
            factory.setUri("amqp://admin:xzkj1225@82.157.251.233:5672");
             connection= factory.newConnection();
            Channel channel = connection.createChannel();
            channel.exchangeDeclare("student", BuiltinExchangeType.DIRECT,true);
            channel.basicPublish("student","student", MessageProperties.TEXT_PLAIN,student.toByteArray());
        }catch (Exception e){
           throw new LmhException("400","连接失败");
        }
    }
}
