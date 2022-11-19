package com.company.demo.util;

import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class BaseEntityTest {

    @Test
    void constructorTest() {
        var base = Mockito.mock(BaseEntity.class);
        assertFalse(base.isDeleted());
    }
}
