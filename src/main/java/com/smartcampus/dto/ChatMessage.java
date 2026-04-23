package com.smartcampus.dto;

import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class ChatMessage {
    private String message;
    private String response;
    private String sessionId;
}
