package com.xzkj.xzmmsbase.rabbitMQ;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeoutException;

public class customerTwo {
    public static void main(String[] args) throws URISyntaxException, NoSuchAlgorithmException, KeyManagementException, IOException, TimeoutException {
        //1.建立连接，通过工厂来创建
        ConnectionFactory factory=new ConnectionFactory();
        factory.setUri("amqp://admin:xzkj1225@82.157.251.233:5672");
        Connection connection = factory.newConnection();
        //2.创建信道
        Channel channel = connection.createChannel();
        //3.声明交换器
        channel.exchangeDeclare("test1", BuiltinExchangeType.DIRECT,true);
        //4.声明队列
        /**
         * String queue: 被绑定的消息队列名，当该消息队列不存在时，将新建该消息队列
         * Boolean durable: 是否持久化消息队列， 该参数持久化的仅为队列，而不包含队列中的消息
         * Boolean exclusive: 该通道是否独占该队列
         * Boolean autoDelete: 消费完成时是否删除队列， 该删除操作在消费者彻底断开连接之后进行。
         * Map<String, Object> arguments：其他配置参数
         */
        channel.queueDeclare("queue1",true,false,false,null);
        //5.绑定队列，交换器，路由见
        channel.queueBind("queue1","test1","test1");
        //6.消息订阅
        /**
         * 启动一个消费者，并返回服务端生成的消费者标识
         * queue:队列名
         * autoAck：true 接收到传递过来的消息后acknowledged（应答服务器），false 接收到消息后不应答服务器
         * deliverCallback： 当一个消息发送过来后的回调接口
         * cancelCallback：当一个消费者取消订阅时的回调接口;取消消费者订阅队列时除了使用{@link Channel#basicCancel}之外的所有方式都会调用该回调方法
         * @return 服务端生成的消费者标识
         */
        channel.basicConsume("queue1", false, new DeliverCallback() {
            @Override
            public void handle(String s, Delivery delivery) throws IOException {
                System.out.println("消费者2收到消息内容："+new String(delivery.getBody()));
                //进行手动应答
                /**
                 * 1.消息的标记 tag，表明消息的唯一标识
                 * 2.是否批量应答 应该不允许批量，防止消息的丢失
                 */
                channel.basicAck(delivery.getEnvelope().getDeliveryTag(),false);
            }
        }, new CancelCallback() {
            @Override
            public void handle(String s) throws IOException {
                System.out.println(s+"消息消费被中断");
            }
        });

    }
}
