package com.pet.project.kafkamessenger.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
public class MessageMetadataDTO extends MessageDTO {
    private LocalDateTime timestamp;
}
