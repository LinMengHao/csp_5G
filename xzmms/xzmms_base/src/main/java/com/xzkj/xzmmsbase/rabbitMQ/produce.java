package com.xzkj.xzmmsbase.rabbitMQ;


import com.rabbitmq.client.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

/**
 * 生产者
 */
public class produce {
    public static void main(String[] args) throws IOException, TimeoutException {
        //1.建立连接，通过工厂创建连接
        ConnectionFactory factory=new ConnectionFactory();
//        简写：factory.setUri("amqp://userName:password@hostName:portNumber/virtualHost");
        factory.setHost("82.157.251.233");
        factory.setPort(5672);
        factory.setUsername("admin");
        factory.setPassword("xzkj1225");
        //不设置虚拟主机默认为"/"
        //长连接，tcp  多个信道开销比多个连接开销小很多
        Connection connection = factory.newConnection();

        //2.创建信道
        Channel channel = connection.createChannel();

        //3.声明交换机
        /**
         * exchange :交换器的名称
         * type : 交换器的类型，常见的有direct,fanout,topic等
         * durable :设置是否持久化。durable设置为true时表示持久化，反之非持久化.持久化可以将交换器存入磁盘，在服务器重启的时候不会丢失相关信息。
         * autoDelete：设置是否自动删除。autoDelete设置为true时，则表示自动删除。自动删除的前提是至少有一个队列或者交换器与这个交换器绑定，之后，所有与这个交换器绑定的队列或者交换器都与此解绑。不能错误的理解—当与此交换器连接的客户端都断开连接时，RabbitMq会自动删除本交换器
         * internal：设置是否内置的。如果设置为true，则表示是内置的交换器，客户端程序无法直接发送消息到这个交换器中，只能通过交换器路由到交换器这种方式。
         * arguments:其它一些结构化的参数，比如：alternate-exchange
         *
         *
         * 持久化过程：1.将交换机设置为可持久化
         *           2.消息发送到交换机，写日志，存磁盘，响应
         *           3.消息发送到持久化队列
         *           防丢失是通过日志，被消费，日志也会删除该条消息
         */
        channel.exchangeDeclare("test1", BuiltinExchangeType.DIRECT,true);

        //4.创建消息
//        String s="Hello world";
        //5.发布消息
        /**
         * String exchange : 交换机名， 当不使用交换机时，传入“”空串。
         * String routingKey :（路由地址） 发布消息的队列， 无论channel绑定那个队列，最终发布消息的队列都有该字串指定
         * AMQP.BasicProperties props ：消息的配置属性，例如 MessageProperties.PERSISTENT_TEXT_PLAIN 表示消息持久化。
         * byte[] body ：消息数据本体， 必须是byte数组
         */
//        channel.basicPublish("test1","test1",MessageProperties.TEXT_PLAIN,s.getBytes(StandardCharsets.UTF_8));

        //循环发送消息
//        Scanner scanner=new Scanner(System.in);
//        while (scanner.hasNext()){
//            String next = scanner.next();
//            channel.basicPublish("test1","test1",MessageProperties.TEXT_PLAIN,next.getBytes(StandardCharsets.UTF_8));
//            System.out.println("生产者生产消息："+next);
//        }
        for (int i = 0; i < 100; i++) {
            channel.basicPublish("test1","test1",MessageProperties.TEXT_PLAIN,("消息"+i).getBytes(StandardCharsets.UTF_8));
        }
    }
}
