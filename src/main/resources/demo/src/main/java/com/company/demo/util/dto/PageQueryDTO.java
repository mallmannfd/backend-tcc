package com.company.demo.util.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class PageQueryDTO extends ListQueryDTO {

    private static final long serialVersionUID = 4980006062478617889L;
    private int page;
    private int size;
}
