package com.company.demo.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.reflect.InvocationTargetException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.company.demo.util.dto.RangeDTO;

@ExtendWith(MockitoExtension.class)
class SpecUtilTest {

    @Mock
    Root<Object> root;
    @Mock
    Join<Object, Object> join;
    @Mock
    Expression<String> strExp;
    @Mock
    Expression<LocalDateTime> ldtExp;
    @Mock
    Predicate predicate;

    @Test
    void constructorTest() {
        var method = SpecUtil.class.getDeclaredConstructors();
        var constructor = method[0];
        constructor.setAccessible(true);
        var exception = assertThrows(InvocationTargetException.class, constructor::newInstance);
        assertEquals("Utility class", exception.getCause().getMessage());
    }

    @Test
    void emptyExpressionsTest() {
        var builder = mock(CriteriaBuilder.class, Mockito.CALLS_REAL_METHODS);
        var predicatesEmpty = new ArrayList<Predicate>();
        
        when(builder.and(any())).thenReturn(predicate);
        assertNull(SpecUtil.toAndArray(builder, predicatesEmpty));
        assertNull(SpecUtil.toOrArray(builder, predicatesEmpty));
        assertNull(SpecUtil.isEqual(builder, strExp, null));
        assertNull(SpecUtil.like(builder, strExp, null));
        assertNull(SpecUtil.like(builder, strExp, ""));
        assertNull(SpecUtil.contains(builder, strExp, null));
        assertNull(SpecUtil.startsWith(builder, strExp, null));
        assertNull(SpecUtil.isIn(root, null));
        assertNull(SpecUtil.isIn(root, Arrays.asList()));
        assertEquals(builder.and(), SpecUtil.isDeleted(builder, root, null));
        assertEquals(builder.and(), SpecUtil.byFilter(builder, root, null));
        assertEquals(builder.and(), SpecUtil.byFilter(builder, root, ""));
    }

    @Test
    void expressionsTest() {
        var predicates = new ArrayList<Predicate>();
        predicates.add(mock(Predicate.class));
        predicates.add(null);

        var builder = mock(CriteriaBuilder.class, Mockito.CALLS_REAL_METHODS);
        when(root.get(anyString())).thenReturn(root);
        when(root.join(anyString(), any())).thenReturn(join);
        when(root.as(String.class)).thenReturn(strExp);
        when(root.in(anyCollection())).thenReturn(predicate);
        when(join.get(anyString())).thenReturn(root);
        when(strExp.as(String.class)).thenReturn(strExp);
        when(builder.concat(strExp, strExp)).thenReturn(strExp);
        when(builder.equal(any(), anyString())).thenReturn(predicate);
        when(builder.equal(any(), anyBoolean())).thenReturn(predicate);
        when(builder.like(any(), anyString())).thenReturn(predicate);
        when(builder.and(any())).thenReturn(predicate);
        when(builder.or(any())).thenReturn(predicate);
        when(builder.literal(anyString())).thenReturn(strExp);
        when(builder.function("translate", String.class, strExp, strExp, strExp)).thenReturn(strExp);

        assertNotNull(SpecUtil.removeAccents(builder, strExp));
        assertNotNull(SpecUtil.toAndArray(builder, predicates));
        assertNotNull(SpecUtil.toOrArray(builder, predicates));
        assertNotNull(SpecUtil.concatAll(builder, strExp, strExp));
        assertNotNull(SpecUtil.isEqual(builder, strExp, "val"));
        assertNotNull(SpecUtil.like(builder, strExp, "val"));
        assertNotNull(SpecUtil.contains(builder, strExp, "val"));
        assertNotNull(SpecUtil.startsWith(builder, strExp, "val"));
        assertNotNull(SpecUtil.isIn(root, Arrays.asList("val1", "val2")));
        assertNotNull(SpecUtil.isDeleted(builder, root, true));
        assertNotNull(SpecUtil.isDeleted(builder, root, false));
        assertNotNull(SpecUtil.byFilter(builder, root, "test", "id", "description"));
        assertNotNull(SpecUtil.byFilter(builder, root, "test", "composition.description"));
    }

    @Test
    void datesTest() {
        var startDate = LocalDate.of(2022, 8, 4);
        var endDate = LocalDate.of(2022, 8, 6);
        var builder = mock(CriteriaBuilder.class, Mockito.CALLS_REAL_METHODS);
        when(builder.greaterThanOrEqualTo(ldtExp, startDate.atStartOfDay())).thenReturn(predicate);
        when(builder.lessThanOrEqualTo(ldtExp, endDate.atTime(LocalTime.MAX))).thenReturn(predicate);
        when(builder.and(any())).thenReturn(predicate);

        assertNull(SpecUtil.isBetween(builder, ldtExp, null));
        assertNotNull(SpecUtil.isBetween(builder, ldtExp, RangeDTO.builder().start(null).end(endDate).build()));
        assertNotNull(SpecUtil.isBetween(builder, ldtExp, RangeDTO.builder().start(startDate).end(null).build()));
        assertNotNull(SpecUtil.isBetween(builder, ldtExp, RangeDTO.builder().start(startDate).end(endDate).build()));
    }
}
