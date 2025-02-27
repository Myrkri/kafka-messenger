package com.pet.project.kafkamessenger.rest;

import com.pet.project.kafkamessenger.dto.MessageDTO;
import com.pet.project.kafkamessenger.dto.MessageMetadataDTO;
import com.pet.project.kafkamessenger.service.MessengerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.List;

@RestController("/api")
@RequiredArgsConstructor
public class MessengerController {

    private final MessengerService messengerService;

    @PostMapping("/send")
    public void sendMessage(@RequestBody MessageDTO message) {
        messengerService.send(message);
    }

    @GetMapping(value = "/get/{sender}")
    public List<MessageMetadataDTO> getMessages(@PathVariable String sender) {
        return messengerService.getMessages(sender);
    }
}
