package com.company.demo.util.dto;

import java.io.Serializable;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class RangeDTO implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 5124845047962594494L;
    private LocalDate start;
    private LocalDate end;
}
