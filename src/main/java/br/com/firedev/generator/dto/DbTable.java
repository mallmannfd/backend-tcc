package br.com.firedev.generator.dto;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.util.ObjectUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;

import br.com.firedev.generator.util.GeneratorUtils;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@ToString
public class DbTable implements Serializable {

    private static final long serialVersionUID = -7834426499346630208L;
    private String name;
    private Boolean isAbstract;
    private Boolean hasChilds;
    private String discriminatorColumn;
    private List<String> discriminatorColumnValues;
    private Map<String, String> discriminatorMapping;
    private List<DbColumn> columns;
    private List<DbIndex> indexes;
    private List<DbRelationship> relationships;

    private String className;
    @JsonIgnore
    private Map<String, DbColumn> attributes;
    @JsonIgnore
    private Map<String, DbRelationship> joins;
    @JsonIgnore
    private DbRelationship isA;
    @JsonIgnore
    private DbTable superTable;
    @JsonIgnore
    private String idName;
    @JsonIgnore
    private String idType;
    @JsonIgnore
    private boolean embId;
    @JsonIgnore
    private DbColumn idCol;

    @JsonIgnore
    public boolean isAbstract() {
        return Boolean.TRUE.equals(this.isAbstract);
    }

    @JsonIgnore
    public boolean hasChilds() {
        return Boolean.TRUE.equals(this.hasChilds);
    }

    @JsonIgnore
    public DbTable calculatedFields() {
        if (ObjectUtils.isEmpty(this.className)) {
            this.className = GeneratorUtils.toSingularUpperCamelCase(this.name);
        }
        this.attributes = this.columns.stream().map(DbColumn::calculatedFields)
                .collect(Collectors.toMap(DbColumn::getName, Function.identity()));
        if (this.relationships != null) {
            this.joins = this.relationships.stream().filter(r -> "belongsTo".equals(r.getType()))
                    .collect(Collectors.toMap(DbRelationship::getColumn, Function.identity()));
            this.isA = this.relationships.stream().filter(r -> "isA".equals(r.getType())).findFirst().orElse(null);
        }
        return this;
    }
}
