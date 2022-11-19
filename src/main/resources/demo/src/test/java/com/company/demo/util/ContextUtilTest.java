package com.company.demo.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.lang.reflect.InvocationTargetException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;

@ExtendWith(MockitoExtension.class)
class ContextUtilTest {

    @Mock
    ApplicationContext applicationContext;

    @Test
    void constructorTest() {
        var method = ContextUtil.class.getDeclaredConstructors();
        var constructor = method[0];
        constructor.setAccessible(true);
        var exception = assertThrows(InvocationTargetException.class, constructor::newInstance);
        assertEquals("Utility class", exception.getCause().getMessage());
    }

    @Test
    void getBeanTest() {
        ContextUtil.setContext(applicationContext);
        assertNotNull(ContextUtil.getContext());
    }
}
