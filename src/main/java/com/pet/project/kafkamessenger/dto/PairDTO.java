package com.pet.project.kafkamessenger.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PairDTO<K, V> {
    private K key;
    private V value;
}
