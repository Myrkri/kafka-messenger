package com.pet.project.kafkamessenger.dto;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class MessageDTO {
    private String message;
    private String sender;
    private String receiver;
}
