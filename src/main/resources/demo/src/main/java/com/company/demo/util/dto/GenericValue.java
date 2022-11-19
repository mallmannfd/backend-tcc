package com.company.demo.util.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GenericValue<T> {

    private T value;

    public static <T> GenericValue<T> of(T value) {
        return new GenericValue<>(value);
    }
}
