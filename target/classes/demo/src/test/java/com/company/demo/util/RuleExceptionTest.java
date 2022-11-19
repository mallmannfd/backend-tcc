package com.company.demo.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.text.MessageFormat;
import java.util.Locale;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.context.support.AbstractMessageSource;

import com.company.demo.util.i18n.MessageFactory;
import com.company.demo.util.i18n.Messages;

@ExtendWith(MockitoExtension.class)
class RuleExceptionTest {

    @Mock
    ApplicationContext applicationContext;

    @Test
    void constructorTest()
            throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        var messageSource = new AbstractMessageSource() {
            protected MessageFormat resolveCode(String code, Locale locale) {
                return new MessageFormat(code, locale);
            }
        };
        when(applicationContext.getBean("messageSource", MessageSource.class)).thenReturn(messageSource);
        var msgSource = MessageFactory.class.getDeclaredField("messageSource");
        msgSource.setAccessible(true);
        msgSource.set(null, null);
        ContextUtil.setContext(applicationContext);
        assertEquals("TestStr", new RuleException("TestStr").getMessage());
        assertEquals("RECORD_NOT_FOUND", new RuleException(Messages.RECORD_NOT_FOUND).getMessage());
        assertEquals("RECORD_NOT_FOUND", new RuleException(Messages.RECORD_NOT_FOUND, "param1").getMessage());
    }
}
