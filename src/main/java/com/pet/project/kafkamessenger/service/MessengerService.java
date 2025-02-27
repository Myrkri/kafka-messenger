package com.pet.project.kafkamessenger.service;

import com.pet.project.kafkamessenger.dto.MessageDTO;
import com.pet.project.kafkamessenger.dto.MessageMetadataDTO;

import java.util.List;

public interface MessengerService {

    void send(MessageDTO message);
    List<MessageMetadataDTO> getMessages(String sender);
}
