package com.smartcampus.controller;

import com.smartcampus.dto.ChatMessage;
import com.smartcampus.service.ChatbotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chatbot")
public class ChatbotController {

    @Autowired
    private ChatbotService chatbotService;

    @PostMapping("/message")
    public ResponseEntity<ChatMessage> processMessage(@RequestBody ChatMessage chatMessage) {
        String response = chatbotService.processMessage(chatMessage.getMessage());
        chatMessage.setResponse(response);
        return ResponseEntity.ok(chatMessage);
    }
}
