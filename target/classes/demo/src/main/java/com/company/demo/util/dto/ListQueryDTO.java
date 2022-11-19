package com.company.demo.util.dto;

import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class ListQueryDTO implements Serializable {

    private static final long serialVersionUID = -8176585448378150244L;
    private List<String> sort;
    private String filter;
}
