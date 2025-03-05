package com.pet.project.kafkamessenger.rest;

import com.pet.project.kafkamessenger.dto.MessageDTO;
import com.pet.project.kafkamessenger.dto.MessageMetadataDTO;
import com.pet.project.kafkamessenger.service.MessengerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<List<MessageMetadataDTO>> getMessages(@PathVariable String sender) {
        if (!StringUtils.hasText(sender)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok().body(messengerService.getMessages(sender));
    }
}
