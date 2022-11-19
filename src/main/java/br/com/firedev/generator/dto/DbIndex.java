package br.com.firedev.generator.dto;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@ToString
public class DbIndex implements Serializable {

    private static final long serialVersionUID = 2536228070167223018L;
    private List<String> fields;
    @JsonProperty("primaryKey")
    @JsonAlias("isPrimaryKey")
    private Boolean primaryKey;
    private String name;
    @JsonProperty("unique")
    @JsonAlias("isUnique")
    private Boolean unique;

    @JsonIgnore
    public boolean isPrimaryKey() {
        return Boolean.TRUE.equals(this.primaryKey);
    }

    @JsonIgnore
    public boolean isUnique() {
        return Boolean.TRUE.equals(this.unique);
    }
}
