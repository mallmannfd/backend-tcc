package com.company.demo.util.dto;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ListDTO<C extends Serializable> implements Serializable {

    private static final long serialVersionUID = -8574700600660578557L;
    private List<C> content;
    private int totalElements;

    public ListDTO(List<C> content) {
        super();
        if (content == null) {
            content = Collections.emptyList();
        }
        this.content = content;
        this.totalElements = content.size();
    }

    public static <C extends Serializable> ListDTO<C> of(List<C> content) {
        return new ListDTO<>(content);
    }
}
