package com.xzkj.xzmmsbase.rabbitMQ.config;


import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.amqp.SimpleRabbitListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
@Slf4j
@Configuration
public class RabbitConfig {
    @Bean
    public RabbitTemplate getRabbitTemplate(ConnectionFactory connectionFactory){
        RabbitTemplate rabbitTemplate=new RabbitTemplate();
        rabbitTemplate.setConnectionFactory(connectionFactory);
        rabbitTemplate.setMandatory(true);

        rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {
            @Override
            public void confirm(CorrelationData correlationData, boolean b, String s) {
                log.info("确认消息到服务端时间："+System.currentTimeMillis());
                log.info("ConfirmCallback:     "+"相关数据："+correlationData);
                log.info("ConfirmCallback:     "+"确认情况："+b);
                log.info("ConfirmCallback:     "+"原因："+s);
            }
        });

        rabbitTemplate.setReturnCallback(new RabbitTemplate.ReturnCallback() {
            @Override
            public void returnedMessage(Message message, int i, String s, String s1, String s2) {
                log.info("消息被服务器退回。msg:{}, replyCode:{}. replyText:{}, exchange:{}, routingKey :{}",
                        new String(message.getBody()), i, s, s1, s2);
            }
        });

        return rabbitTemplate;
    }


    //监听容器工厂
    @Bean("studentContainer")
    public SimpleRabbitListenerContainerFactory simpleRabbitListenerContainerFactory(
        SimpleRabbitListenerContainerFactoryConfigurer configurer,
        ConnectionFactory connectionFactory){
        SimpleRabbitListenerContainerFactory container=new SimpleRabbitListenerContainerFactory();
        //多消费者处理统一队列的消息（多线程监听）
        container.setConcurrentConsumers(50);
        //container.setConcurrentConsumers(1);
        //最大多线程监听数量
        container.setMaxConcurrentConsumers(50);
        //使用自定义线程池来启动消费者。
        //container.setTaskExecutor(taskExecutor);
        //是否重返队列
        container.setDefaultRequeueRejected(true);
        //手动
        container.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        configurer.configure(container,connectionFactory);
        //使用protocol buffer 不需要序列化器，使用字节数组传输
        return container;
    }
}
