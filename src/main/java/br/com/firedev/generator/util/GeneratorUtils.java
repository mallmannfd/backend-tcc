package br.com.firedev.generator.util;

import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import org.springframework.util.ResourceUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.CaseFormat;

import br.com.firedev.generator.dto.DbColumn;
import br.com.firedev.generator.dto.DbIndex;
import br.com.firedev.generator.dto.DbTable;
import br.com.firedev.generator.dto.DbTableList;
import br.com.firedev.generator.dto.MemoryFileTree;
import br.com.firedev.generator.dto.Template;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GeneratorUtils {
    private static final String APP_PKG_VAL = "com.company.demo";
    private static final String LOG_ERROR = "Error {}: {}";
    private static final Map<String, String> DB_JAVA_TYPES;
    private static Random random;
    static {
        var map = new HashMap<String, String>();
        map.put("int", "Integer");
        map.put("smallint", "Short");
        map.put("bigint", "Long");
        map.put("double", "Double");
        map.put("float", "Float");
        map.put("bit", "Boolean");
        map.put("decimal", "BigDecimal");
        map.put("timestamp", "LocalDateTime");
        map.put("binary", "byte[]");
        map.put("blob", "byte[]");
        map.put("varchar", "String");
        map.put("varchar2", "String");
        map.put("text", "String");
        map.put("enum", "String");
        DB_JAVA_TYPES = Collections.unmodifiableMap(map);
    }

    private GeneratorUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static Random getRandom() {
        if (random == null) {
            try {
                random = SecureRandom.getInstanceStrong();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
                random = new Random();
            }
        }
        return random;
    }

    public static String toLowerCamelCase(String str) {
        return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, str);
    }

    public static String toUpperCamelCase(String str) {
        return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, str);
    }

    public static String getJavaType(String dbType) {
        var javaType = dbType == null ? null : DB_JAVA_TYPES.get(dbType.toLowerCase());
        return javaType == null ? GeneratorUtils.toUpperCamelCase(dbType) : javaType;
    }

    /***
     * var stemmer = new PorterStemmer(); stemmer.setCurrent(upperCamel);
     * stemmer.stem(); return stemmer.getCurrent();
     */
    public static String toSingularUpperCamelCase(String str) {
        var upperCamel = toUpperCamelCase(str);
        if (upperCamel.endsWith("s")) {
            return upperCamel.substring(0, upperCamel.length() - 1);
        }
        return upperCamel;
    }

    public static DbTableList readJson(String jsonContent) throws IOException {
        var mapper = new ObjectMapper();
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        var list = mapper.readValue(new StringReader(jsonContent), new TypeReference<List<DbTable>>() {
        });
        return new DbTableList(list);
    }

    public static DbTableList readJson() throws IOException {
        var json = ResourceUtils.getFile("classpath:simpledb.json");
        var jsonContent = new String(Files.readAllBytes(json.toPath()));
        return readJson(jsonContent);
    }

    public static MemoryFileTree buildSource(MemoryFileTree memFiles, DbTableList dbTableList) throws IOException {
        var appNode = memFiles.get("src").get("main").get("java").get("com").get("company").get("demo");
        var testNode = memFiles.get("src").get("test").get("java").get("com").get("company").get("demo");
        var modelNode = appNode.getOrCreateDir("model");
        var repositoryNode = appNode.getOrCreateDir("repository");
        var serviceNode = appNode.getOrCreateDir("service");
        var controllerNode = appNode.getOrCreateDir("controller");
        var specNode = appNode.getOrCreateDir("spec");
        var dtoNode = appNode.getOrCreateDir("dto");
        var reqDtoNode = dtoNode.getOrCreateDir("request");
        var resDtoNode = dtoNode.getOrCreateDir("response");
        var mapperNode = appNode.getOrCreateDir("mapper");
        var testServiveNode = testNode.getOrCreateDir("service");
        var tables = dbTableList.getTables();
        var random = getRandom();
        for (var table : tables) {
            log.info(table.getName());
            var fields = new StringBuilder();
            var dtoReqFields = new StringBuilder();
            var dtoResFields = new StringBuilder();
            var columnsBuilder = new StringBuilder();
            var testObjBuilder = new StringBuilder();
            var idExample = "";
            var modelName = table.getClassName();
            var idType = getIdType(table, modelNode, fields);
            buildCols(table, appNode, fields, dtoReqFields, dtoResFields, columnsBuilder, testObjBuilder);
            var columns = "";
            if (columnsBuilder.length() > 2) {
                columns = columnsBuilder.substring(2);
            } else {
                columns = columnsBuilder.toString();
            }
            var inheritance = buildInheritance(table);
            var superTableName = buildSuperName(table, idType);
            var replaceMap = new HashMap<String, String>();
            var abstr = table.isAbstract() ? "abstract " : "";
            if ("Integer".equals(idType)) {
                idExample = "" + random.nextInt();
            } else if ("Long".equals(idType)) {
                idExample = "" + random.nextLong();
            } else {
                idExample = String.format("new %s(1, 1)", idType);
            }
            replaceMap.put("{app_pkg}", APP_PKG_VAL);
            replaceMap.put("{abstract}", abstr);
            replaceMap.put("{table_name}", table.getName());
            replaceMap.put("{inheritance}", inheritance);
            replaceMap.put("{model_name}", modelName);
            replaceMap.put("{super}", superTableName);
            replaceMap.put("{id_type}", idType);
            replaceMap.put("{fields}", fields.toString());
            replaceMap.put("{dtoReqFields}", dtoReqFields.toString());
            replaceMap.put("{dtoResFields}", dtoResFields.toString());
            replaceMap.put("{columns}", columns);
            replaceMap.put("{endpoint}", table.getName().replace("_", "-"));
            replaceMap.put("{test_obj}", testObjBuilder.toString());
            replaceMap.put("{id_example}", idExample);
            if (table.isEmbId()) {
                replaceMap.put("@PathVariable(\"id\") ", "");
                replaceMap.put("/{id}", "/");
            }
            replaceSouces(replaceMap, Template.MODEL, modelNode, modelName);
            if (!table.hasChilds()) {
                replaceSouces(replaceMap, Template.REPOSITORY, repositoryNode, modelName);
                replaceSouces(replaceMap, Template.SERVICE, serviceNode, modelName);
                replaceSouces(replaceMap, Template.SPEC, specNode, modelName);
                replaceSouces(replaceMap, Template.CONTROLLER, controllerNode, modelName);
                replaceSouces(replaceMap, Template.DTO_REQ, reqDtoNode, modelName);
                replaceSouces(replaceMap, Template.DTO_RES, resDtoNode, modelName);
                replaceSouces(replaceMap, Template.MAPPER, mapperNode, modelName);
                replaceSouces(replaceMap, Template.SERVICE_TEST, testServiveNode, modelName);
            }
        }
        return memFiles;
    }

    public static void buildCols(DbTable table, MemoryFileTree appNode, StringBuilder fields,
            StringBuilder dtoReqFields, StringBuilder dtoResFields, StringBuilder columnsBuilder,
            StringBuilder testObjBuilder) {
        var idCol = table.getIdCol();
        if (idCol != null) {
            var formatId = String.format("%s    private %s %s;", System.lineSeparator(), idCol.getJavaType(),
                    idCol.getAttributeName());
            dtoResFields.append(formatId);
            if (table.isEmbId() || !idCol.isAutoIncrement()) {
                dtoReqFields.append(formatId);
            }
        }
        var superTable = table.getSuperTable();
        if (superTable != null && superTable.getColumns() != null) {
            for (var col : superTable.getColumns()) {
                if (!col.isPrimaryKey()) {
                    dtoReqFields.append(buildDtoCol(col));
                    dtoResFields.append(buildDtoCol(col));
                }
                generateTestObjCol(testObjBuilder, col);
            }
        }
        for (var col : table.getColumns()) {
            if (!col.isPrimaryKey()) {
                fields.append(buildCol(col, appNode));
                dtoReqFields.append(buildDtoCol(col));
                dtoResFields.append(buildDtoCol(col));
            }
            columnsBuilder.append(", ").append("\"").append(col.getAttributeName()).append("\"");
            generateTestObjCol(testObjBuilder, col);
        }
    }

    public static void generateTestObjCol(StringBuilder testObjBuilder, DbColumn col) {
        if (!col.isAutoIncrement() && col.getForeignTable() == null && !"enum".equals(col.getType())
                && (col.isUnique() || col.isNotNull())) {
            var cname = CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_CAMEL, col.getAttributeName());
            if ("String".equals(col.getJavaType())) {
                testObjBuilder.append(
                        String.format("%s\tpojo.set%s(\"test_\" + UUID.randomUUID());", System.lineSeparator(), cname));
            } else if ("Integer".equals(col.getJavaType())) {
                testObjBuilder.append(
                        String.format("%s\tpojo.set%s(new Random().nextInt());", System.lineSeparator(), cname));
            } else if ("Long".equals(col.getJavaType())) {
                testObjBuilder.append(
                        String.format("%s\tpojo.set%s(new Random().nextLong());", System.lineSeparator(), cname));
            } else if ("Float".equals(col.getJavaType())) {
                testObjBuilder.append(
                        String.format("%s\tpojo.set%s(new Random().nextFloat());", System.lineSeparator(), cname));
            } else if ("Boolean".equals(col.getJavaType())) {
                testObjBuilder.append(String.format("%s\tpojo.set%s(true);", System.lineSeparator(), cname));
            } else if (!"LocalDateTime".equals(col.getJavaType())) {
                testObjBuilder.append(String.format("%s\tpojo.set%s(new %s(1, 1));", System.lineSeparator(), cname,
                        col.getJavaType()));
            }
        }
    }

    public static String buildSuperName(DbTable table, String idType) {
        var superTableName = "BaseEntity<" + idType + ">";
        if (table.getSuperTable() != null) {
            superTableName = table.getSuperTable().getClassName();
        }
        return superTableName;
    }

    public static String buildInheritance(DbTable table) {
        var inheritance = "";
        if (table.getSuperTable() != null) {
            var superTable = table.getSuperTable();
            if (!superTable.isAbstract()) {
                inheritance = "@PrimaryKeyJoinColumn(name=\"" + table.getIdName() + "\")";
            }
        }
        if (table.hasChilds()) {
            if (table.isAbstract()) {
                inheritance = System.lineSeparator() + "@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)";
            } else {
                inheritance = System.lineSeparator() + "@Inheritance(strategy = InheritanceType.JOINED)";
            }
        }
        return inheritance;
    }

    public static String buildCol(DbColumn col, MemoryFileTree appNode) {
        var fields = new StringBuilder();
        var javaType = col.getJavaType();
        var colNotation = "";
        if (col.getForeignTable() != null) {
            colNotation = String.format("%s    @ManyToOne(fetch = FetchType.LAZY)", System.lineSeparator());
            colNotation += String.format("%s    @JoinColumn(name = \"%s\")", System.lineSeparator(), col.getName());
            fields.append(colNotation);
            fields.append(String.format("%s    private %s %s;", System.lineSeparator(), javaType,
                    col.getName().replace("_id", "")));
        } else {
            colNotation = buildSimpleColNotation(col);
            fields.append(colNotation);
            if (col.getType().equals("enum") && !col.getEnumValues().isEmpty()) {
                fields.append(String.format("%s    @Enumerated(EnumType.STRING)", System.lineSeparator()));
                javaType = buildEnum(col, appNode);
                col.setJavaType(javaType);
            }
            fields.append(
                    String.format("%s    private %s %s;", System.lineSeparator(), javaType, col.getAttributeName()));
        }
        return fields.toString();
    }

    public static String buildDtoCol(DbColumn col) {
        var fields = new StringBuilder();
        var javaType = col.getJavaType();
        if (col.getForeignTable() != null && !col.isPrimaryKey()) {
            fields.append(String.format("%s    private OptionDTO<%s> %s;", System.lineSeparator(),
                    col.getForeignTable().getIdType(), col.getName().replace("_id", "")));
        } else {
            fields.append(
                    String.format("%s    private %s %s;", System.lineSeparator(), javaType, col.getAttributeName()));
        }
        return fields.toString();
    }

    public static String buildSimpleColNotation(DbColumn col) {
        var colNotation = String.format("%s    @Column(name = \"%s\"", System.lineSeparator(), col.getName());
        if (col.getNotNull() != null) {
            colNotation += String.format(", nullable = %b", !col.getNotNull());
        }
        if (col.getSize() != null) {
            colNotation += String.format(", length = %d", col.getSize());
        }
        if (col.isUnique()) {
            colNotation += String.format(", unique = %b", col.getUnique());
        }
        var dbType = col.getType();
        if (col.getExpression() != null) {
            if (dbType.equals("enum")) {
                dbType = "VARCHAR";
            }
            colNotation += String.format(", columnDefinition = \"%s %s\"", dbType.toUpperCase(), col.getExpression());
        }
        colNotation += ")";
        return colNotation;
    }

    public static String buildEnum(DbColumn col, MemoryFileTree appNode) {
        var domainNode = appNode.getOrCreateDir("domain");
        var enumName = toUpperCamelCase(col.getAttributeName());
        var enumValues = col.getEnumValues().stream().map(String::toUpperCase).collect(Collectors.joining(", "));
        var replaceMap = new HashMap<String, String>();
        replaceMap.put("{app_pkg}", APP_PKG_VAL);
        replaceMap.put("{enum_name}", enumName);
        replaceMap.put("{values}", enumValues);
        replaceSouces(replaceMap, Template.DOMAIN, domainNode, enumName);
        return enumName + "Enum";
    }

    public static String getIdType(DbTable table, MemoryFileTree modelNode, StringBuilder fields) {
        var idType = "";
        var indexes = table.getIndexes().stream().filter(DbIndex::isPrimaryKey).toList();
        if (!indexes.isEmpty()) {
            var index = indexes.get(0);
            if (index.getFields().size() == 1) {
                idType = buildId(table, index, fields);
            } else if (index.getFields().size() > 1) {
                table.setEmbId(true);
                idType = buildEmbeddedId(table, index, modelNode, fields);
            }
        }
        table.setIdType(idType);
        return idType;
    }

    public static DbColumn findIdCol(DbTable table, DbIndex index) {
        var col = index.getFields().isEmpty() ? null : table.getAttributes().get(index.getFields().get(0));
        if (col == null && table.getSuperTable() != null) {
            return findIdCol(table.getSuperTable(), index);
        }
        return col;
    }

    public static String buildId(DbTable table, DbIndex index, StringBuilder fields) {
        var col = findIdCol(table, index);
        table.setIdCol(col);
        if (col == null) {
            return "";
        }
        var idType = col.getJavaType();
        if (table.getSuperTable() != null && table.getSuperTable().isAbstract()) {
            return idType;
        }
        table.setIdName(col.getAttributeName());
        if (fields != null) {
            var strategy = "GenerationType.IDENTITY";
            if (table.isAbstract()) {
                strategy = "GenerationType.TABLE";
            }
            fields.append(String.format("%s    @Id", System.lineSeparator()));
            if (col.isAutoIncrement()) {
                fields.append(String.format("%s    @GeneratedValue(strategy = %s)", System.lineSeparator(), strategy));
            }
            fields.append(buildCol(col, null));
        }
        col.setJavaType(idType);
        return idType;
    }

    public static String buildEmbeddedId(DbTable table, DbIndex index, MemoryFileTree modelNode, StringBuilder fields) {
        table.setIdName("id");
        var modelName = table.getClassName();
        var idType = modelName + "Id";
        if (modelNode != null && fields != null) {
            var idFields = new StringBuilder();
            for (var idx : index.getFields()) {
                var col = table.getAttributes().get(idx);
                var javaType = col.getForeignTable().getIdType();
                idFields.append(String.format("%s    @Column(name = \"%s\")", System.lineSeparator(), col.getName()));
                idFields.append(String.format("%s    private %s %s;", System.lineSeparator(), javaType,
                        col.getAttributeName()));
            }
            var replaceMap = new HashMap<String, String>();
            replaceMap.put("{app_pkg}", APP_PKG_VAL);
            replaceMap.put("{model_name}", modelName);
            replaceMap.put("{fields}", idFields.toString());
            replaceSouces(replaceMap, Template.EMB_ID, modelNode, modelName);
            fields.append(String.format("%s    @EmbeddedId", System.lineSeparator()));
            fields.append(String.format("%s    private %s id;", System.lineSeparator(), idType));
        }
        var idCol = new DbColumn();
        idCol.setJavaType(idType);
        idCol.setName(table.getIdName());
        idCol.setAttributeName(table.getIdName());
        table.setIdCol(idCol);
        return idType;
    }

    public static void replaceSouces(Map<String, String> replaceMap, Template template, MemoryFileTree node,
            String modelName) {
        try (var lines = Files.lines(ResourceUtils.getFile(template.getPath()).toPath())) {
            var tptContent = lines.collect(Collectors.joining(System.lineSeparator()));
            tptContent += System.lineSeparator();
            for (var entry : replaceMap.entrySet()) {
                tptContent = tptContent.replace(entry.getKey(), entry.getValue());
            }
            var fileName = String.format(template.getFileNamePattern(), modelName);
            node.putFile(fileName, tptContent);
        } catch (IOException e) {
            log.error(LOG_ERROR, modelName, e.getMessage());
        }
    }
}
