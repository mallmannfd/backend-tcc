package br.com.firedev.generator.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;

import br.com.firedev.generator.dto.DbTable;
import br.com.firedev.generator.dto.MemoryFileTree;
import br.com.firedev.generator.util.GeneratorUtils;

@RestController
@RequestMapping("/")
public class IndexController {

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public String index() {
        return "Spring Generator API!";
    }

    @PostMapping(value = "/generate-project", produces = "application/zip", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public byte[] zipFiles(HttpServletResponse response, @RequestBody JsonNode jsonContent) throws IOException {
        response.addHeader("Content-Disposition", "attachment; filename=\"demo.zip\"");
        var demo = ResourceUtils.getFile("classpath:demo");
        var memFiles = new MemoryFileTree(demo);
        var dbTableList = GeneratorUtils.readJson(jsonContent.toString());
        GeneratorUtils.buildSource(memFiles, dbTableList);
        return memFiles.toZip();
    }

    @GetMapping(value = "/generate-project/example", produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public List<DbTable> getJsonExample() throws IOException {
        return GeneratorUtils.readJson().getTables();
    }

    @PostMapping(value = "/generate-project/example", produces = "application/zip")
    @ResponseStatus(HttpStatus.OK)
    public byte[] getZipExample(HttpServletResponse response) throws IOException {
        var dbTableList = GeneratorUtils.readJson();
        response.addHeader("Content-Disposition", "attachment; filename=\"demo.zip\"");
        var demo = ResourceUtils.getFile("classpath:demo");
        var memFiles = new MemoryFileTree(demo);
        GeneratorUtils.buildSource(memFiles, dbTableList);
        return memFiles.toZip();
    }

}
