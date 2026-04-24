package com.smartcampus.controller;

import com.smartcampus.dto.ChatMessage;
import com.smartcampus.service.ChatbotService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chatbot")
@RequiredArgsConstructor
public class ChatbotController {

    private final ChatbotService chatbotService;

    @PostMapping("/message")
    public ResponseEntity<ChatMessage> processMessage(@RequestBody ChatMessage chatMessage) {
        String response = chatbotService.processMessage(chatMessage.getMessage());
        chatMessage.setResponse(response);
        return ResponseEntity.ok(chatMessage);
    }
}
