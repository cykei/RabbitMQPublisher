package com.example.rabbitmqpublisher.stomp;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class MessageController {
    private final SimpMessageSendingOperations simpleMessageSendingOperations;

    @MessageMapping("/hello")
    public void message(Message message){
        simpleMessageSendingOperations.convertAndSend("/sub/channel/" + message.getChannelId(), message);
    }
}
