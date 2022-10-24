package com.xzkj.accessService.rabbitMQ.conf;


import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.amqp.SimpleRabbitListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;

@Slf4j
@Configuration
public class RabbitConfig {
    @Autowired
    @Qualifier("TaskExecutor")
    TaskExecutor taskExecutor;
    public static final String CONTAINER_FACTORY_ACCESS="AccessContainerFactory";
    @Bean
    public RabbitTemplate createRabbitTemplate(ConnectionFactory connectionFactory){
        RabbitTemplate rabbitTemplate = new RabbitTemplate();
        rabbitTemplate.setConnectionFactory(connectionFactory);
//        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
        //设置开启Mandatory,才能触发回调函数,无论消息推送结果怎么样都强制调用回调函数
        rabbitTemplate.setMandatory(true);

        rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {
            @Override
            public void confirm(CorrelationData correlationData, boolean ack, String cause) {
                log.info("确认消息到服务端时间："+System.currentTimeMillis());
                log.info("ConfirmCallback:     "+"相关数据："+correlationData);
                log.info("ConfirmCallback:     "+"确认情况："+ack);
                log.info("ConfirmCallback:     "+"原因："+cause);
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

    @Bean(CONTAINER_FACTORY_ACCESS)
    public SimpleRabbitListenerContainerFactory containerFactory(
            SimpleRabbitListenerContainerFactoryConfigurer configurer,
            ConnectionFactory connectionFactory
    ){
        SimpleRabbitListenerContainerFactory container = new SimpleRabbitListenerContainerFactory();
        //多消费者处理统一队列的消息（多线程监听）
        container.setConcurrentConsumers(50);
//        container.setConcurrentConsumers(1);
        //最大多线程监听数量
        container.setMaxConcurrentConsumers(50);
//        container.setMaxConcurrentConsumers(1);
        configurer.configure(container,connectionFactory);
        //限流 单位时间内消费多少条记录
        container.setPrefetchCount(50);
        //使用自定义线程池来启动消费者。
//        container.setTaskExecutor(taskExecutor);
        //是否重返队列
        container.setDefaultRequeueRejected(true);
        //手动确认
        container.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        container.setMessageConverter(new Jackson2JsonMessageConverter());
        return container;
    }

}
