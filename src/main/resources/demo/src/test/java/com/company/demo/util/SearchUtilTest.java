package com.company.demo.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

import com.company.demo.util.dto.PageQueryDTO;

@ExtendWith(MockitoExtension.class)
class SearchUtilTest {

    @Test
    void constructorTest() {
        var method = SearchUtil.class.getDeclaredConstructors();
        var constructor = method[0];
        constructor.setAccessible(true);
        var exception = assertThrows(InvocationTargetException.class, constructor::newInstance);
        assertEquals("Utility class", exception.getCause().getMessage());
    }

    @Test
    void toPageableTest() {
        var queryDTO = new PageQueryDTO();
        queryDTO.setPage(0);
        queryDTO.setSize(50);
        var pageable = SearchUtil.toPageable(queryDTO);
        assertNotNull(pageable);
        assertEquals(0, pageable.getPageNumber());
        assertEquals(50, pageable.getPageSize());
        assertEquals(Sort.unsorted(), pageable.getSort());
    }

    @Test
    void toPageableZeroSizeTest() {
        var queryDTO = new PageQueryDTO();
        queryDTO.setPage(0);
        queryDTO.setSize(0);
        var pageable = SearchUtil.toPageable(queryDTO);
        assertNotNull(pageable);
        assertEquals(0, pageable.getPageNumber());
        assertEquals(Integer.MAX_VALUE, pageable.getPageSize());
        assertEquals(Sort.unsorted(), pageable.getSort());
    }

    @Test
    void toSortTest() {
        var sortList = Arrays.asList("id", "name:asc", "description:desc");
        var sort = SearchUtil.toSort(sortList);
        assertNotNull(sort);
        assertTrue(sort.isSorted());
        assertEquals(Direction.ASC, sort.getOrderFor("name").getDirection());
        assertEquals(Direction.DESC, sort.getOrderFor("description").getDirection());
    }

    @Test
    void toSortEmptyTest() {
        var sort = SearchUtil.toSort(Collections.emptyList());
        assertNotNull(sort);
        assertFalse(sort.isSorted());
        assertEquals(Sort.unsorted(), sort);
    }
}
