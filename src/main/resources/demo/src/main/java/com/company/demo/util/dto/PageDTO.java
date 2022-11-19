package com.company.demo.util.dto;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.domain.Page;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PageDTO<T extends Serializable> implements Serializable {

    private static final long serialVersionUID = -4357088825628356261L;
    private List<T> content;
    private long totalElements;
    private int totalPages;
    private int number;

    public PageDTO(Page<T> page) {
        this.content = page.getContent();
        this.totalElements = page.getTotalElements();
        this.number = page.getNumber();
        this.totalPages = page.getTotalPages();
    }
}
