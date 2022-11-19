package br.com.firedev.generator.dto;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import br.com.firedev.generator.util.GeneratorUtils;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DbTableList implements Serializable {

    private static final long serialVersionUID = 4758849498261424716L;
    private List<DbTable> tables;
    private Map<String, DbTable> models;

    public DbTableList(List<DbTable> tables) {
        super();
        this.tables = tables;
        this.models = tables.stream().map(DbTable::calculatedFields)
                .collect(Collectors.toMap(DbTable::getName, Function.identity()));
        for (var table : tables) {
            if (table.getIsA() != null) {
                var superTable = this.models.get(table.getIsA().getForeignTable());
                table.setSuperTable(superTable);
            }
            for (var col : table.getColumns()) {
                var join = table.getJoins() == null ? null : table.getJoins().get(col.getName());
                if (join != null) {
                    col.setForeignTable(models.get(join.getForeignTable()));
                    col.setJavaType(col.getForeignTable().getClassName());
                }
                GeneratorUtils.getIdType(table, null, null);
            }
        }
    }
}
