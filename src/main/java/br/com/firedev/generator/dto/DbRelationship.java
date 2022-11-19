package br.com.firedev.generator.dto;

import java.io.Serializable;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@ToString
public class DbRelationship implements Serializable {

    private static final long serialVersionUID = -4077265920360011002L;
    private String type;
    private String column;
    private String foreignTable;
    private String foreignColumn;
}
