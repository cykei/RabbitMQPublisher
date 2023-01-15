package com.example.rabbitmqpublisher.rabbitmq;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;

@RestController
public class SampleController {

    private static final String EXCHANGE_NAME = "sample.exchange";

    private final RabbitTemplate rabbitTemplate;
    private final RabbitAdmin rabbitAdmin;

    public SampleController(RabbitTemplate rabbitTemplate, RabbitAdmin rabbitAdmin) {
        this.rabbitTemplate = rabbitTemplate;
        this.rabbitAdmin = rabbitAdmin;
    }

    @GetMapping("/sample/queue/{key}/{msg}")
    public String topicPublish(@PathVariable String key, @PathVariable String msg) {
        rabbitTemplate.convertAndSend(EXCHANGE_NAME, "sample.cykei."+key, "message - " + msg);
        System.out.println(key +", " + msg);
        return "message sending!";
    }

    @GetMapping("/default/{msg}")
    public String defaultExchange(@PathVariable String msg) {
        rabbitAdmin.declareQueue(new Queue("hello"));
        rabbitTemplate.send("", "hello", new Message(msg.getBytes(StandardCharsets.UTF_8)));
        return "message send to default exchange";
    }
}