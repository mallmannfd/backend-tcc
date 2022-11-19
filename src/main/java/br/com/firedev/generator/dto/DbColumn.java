package br.com.firedev.generator.dto;

import java.io.Serializable;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import br.com.firedev.generator.util.GeneratorUtils;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@ToString
public class DbColumn implements Serializable {

    private static final long serialVersionUID = 1305337021238766330L;
    private String name;
    private String type;
    @JsonProperty("primaryKey")
    @JsonAlias("isPrimaryKey")
    private Boolean primaryKey;
    @JsonProperty("notNull")
    @JsonAlias("isNotNull")
    private Boolean notNull;
    @JsonProperty("unique")
    @JsonAlias("isUnique")
    private Boolean unique;
    @JsonProperty("autoIncrement")
    @JsonAlias("isAutoIncrement")
    private Boolean autoIncrement;
    private String expression;
    private List<String> enumValues;

    private Integer size;
    private String attributeName;
    private String javaType;
    @JsonIgnore
    private DbTable foreignTable;

    @JsonIgnore
    public boolean isPrimaryKey() {
        return Boolean.TRUE.equals(this.primaryKey);
    }

    @JsonIgnore
    public boolean isNotNull() {
        return Boolean.TRUE.equals(this.notNull);
    }

    @JsonIgnore
    public boolean isUnique() {
        return Boolean.TRUE.equals(this.unique);
    }

    @JsonIgnore
    public boolean isAutoIncrement() {
        return Boolean.TRUE.equals(this.autoIncrement);
    }

    @JsonIgnore
    public DbColumn calculatedFields() {
        if (this.attributeName == null) {
            this.attributeName = GeneratorUtils.toLowerCamelCase(this.name);
        }
        if (this.size == null) {
            var sizeStr = StringUtils.substringBetween(this.type, "(", ")");
            if (sizeStr != null) {
                this.size = Integer.valueOf(sizeStr);
            }
        }
        if (this.javaType == null) {
            var typePart = StringUtils.substringBefore(this.type, "(");
            this.javaType = GeneratorUtils.getJavaType(typePart);
        }
        return this;
    }
}
