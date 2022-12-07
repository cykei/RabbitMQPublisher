package com.example.rabbitmqpublisher;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SampleController {

    private static final String EXCHANGE_NAME = "sample.exchange";

    private final RabbitTemplate rabbitTemplate;

    public SampleController(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @GetMapping("/sample/queue/{key}/{text}")
    public String samplePublish(@PathVariable(name = "key") String key, @PathVariable(name = "text") String text) {
        rabbitTemplate.convertAndSend(EXCHANGE_NAME, "sample.cykei."+key, "message - " + text);
        System.out.println(key +", " + text);
        return "message sending!";
    }
}