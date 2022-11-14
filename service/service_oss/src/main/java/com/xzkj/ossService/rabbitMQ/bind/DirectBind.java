package com.xzkj.ossService.rabbitMQ.bind;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;



@Configuration
public class DirectBind {
    private static final String DIRECT_STUDENT_QUEUE="student";
    private static final String DIRECT_STUDENT_EXCHANGE="student";
    private static final String DIRECT_STUDENT_ROUTEKEY="student";

    @Bean
    public Queue studentQueue(){
        return new Queue(DIRECT_STUDENT_QUEUE,true);
    }
    @Bean
    public DirectExchange studentExchange(){
        return new DirectExchange(DIRECT_STUDENT_EXCHANGE,true,false);
    }
    @Bean
    public Binding studentBinding(){
        return BindingBuilder.bind(studentQueue()).to(studentExchange()).with(DIRECT_STUDENT_ROUTEKEY);
    }
}
