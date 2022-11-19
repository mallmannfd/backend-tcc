package com.company.demo.util.i18n;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import java.util.Locale;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.support.AbstractMessageSource;

import com.company.demo.util.ContextUtil;

@ExtendWith(MockitoExtension.class)
class MessageFactoryTest {

    @Mock
    ApplicationContext applicationContext;

    @Test
    void constructorTest() {
        var method = MessageFactory.class.getDeclaredConstructors();
        var constructor = method[0];
        constructor.setAccessible(true);
        var exception = assertThrows(InvocationTargetException.class, constructor::newInstance);
        assertEquals("Utility class", exception.getCause().getMessage());
    }

    @Test
    void getMessageTest()
            throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        var messageSource = new AbstractMessageSource() {
            protected MessageFormat resolveCode(String code, Locale locale) {
                if (Messages.RECORD_NOT_FOUND.toString().equals(code)) {
                    return new MessageFormat("Test1");
                }
                throw new NoSuchMessageException(code, locale);
            }
        };
        when(applicationContext.getBean("messageSource", MessageSource.class)).thenReturn(messageSource);
        var msgSource = MessageFactory.class.getDeclaredField("messageSource");
        msgSource.setAccessible(true);
        msgSource.set(null, null);
        ContextUtil.setContext(applicationContext);
        assertEquals("Test1", MessageFactory.getMessage(Messages.RECORD_NOT_FOUND));
        assertEquals("testEx", MessageFactory.getMessage("testEx"));
        assertEquals("testLb", MessageFactory.getLabel("testLb"));
    }
}
