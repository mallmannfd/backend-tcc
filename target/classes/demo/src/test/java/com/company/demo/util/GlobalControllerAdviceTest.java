package com.company.demo.util;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

import javax.validation.ConstraintViolationException;

import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessResourceUsageException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;

class GlobalControllerAdviceTest {

    @Test
    void constructorTest() {
        var advice = new GlobalControllerAdvice();
        var request = mock(WebRequest.class);
        var result = mock(BindingResult.class);

        assertNotNull(advice.unknownException(new RuleException("test"), request));
        assertNotNull(advice.unknownException(new ConstraintViolationException("test2", null), request));
        assertNotNull(advice.unknownException(new InvalidDataAccessResourceUsageException("test3",
                new RuntimeException("test3.1", new RuleException("test3.2"))), request));
        assertNotNull(advice.unknownException(new DataIntegrityViolationException("test4",
                new RuntimeException("test4.1", new RuleException("test4.2"))), request));
        assertNotNull(advice.handleMethodArgumentNotValid(new MethodArgumentNotValidException(null, result), null,
                HttpStatus.EXPECTATION_FAILED, request));
    }
}
