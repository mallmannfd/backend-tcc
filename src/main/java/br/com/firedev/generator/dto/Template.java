package br.com.firedev.generator.dto;

import java.io.File;

import lombok.Getter;

@Getter
public enum Template {
    MODEL("classpath:templates/template_model.txt", "%s.java"),
    EMB_ID("classpath:templates/template_emb_id.txt", "%sId.java"),
    REPOSITORY("classpath:templates/template_repository.txt", "%sRepository.java"),
    SERVICE("classpath:templates/template_service.txt", "%sService.java"),
    SPEC("classpath:templates/template_spec.txt", "%sSpec.java"),
    CONTROLLER("classpath:templates/template_controller.txt", "%sController.java"),
    DTO_REQ("classpath:templates/template_req_dto.txt", "%sReqDTO.java"),
    DTO_RES("classpath:templates/template_res_dto.txt", "%sResDTO.java"),
    MAPPER("classpath:templates/template_mapper.txt", "%sMapper.java"),
    DOMAIN("classpath:templates/template_enum.txt", "%sEnum.java"),
    SERVICE_TEST("classpath:templates/template_test_service.txt", "%sServiceTest.java");

    private String path;
    private String fileNamePattern;

    private Template(String path, String fileNamePattern) {
        this.path = path == null ? "" : path.replace("/", File.separator);
        this.fileNamePattern = fileNamePattern;
    }
}
