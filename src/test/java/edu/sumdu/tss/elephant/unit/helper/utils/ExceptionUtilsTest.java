package edu.sumdu.tss.elephant.unit.helper.utils;

import edu.sumdu.tss.elephant.controller.LoginController;
import edu.sumdu.tss.elephant.helper.Keys;
import edu.sumdu.tss.elephant.helper.exception.BackupException;
import edu.sumdu.tss.elephant.helper.utils.ExceptionUtils;
import io.javalin.core.validation.ValidationError;
import io.javalin.core.validation.ValidationException;
import io.javalin.http.Context;
import io.javalin.http.util.ContextUtil;
import kotlin.reflect.jvm.internal.ReflectProperties;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.annotation.ExpectedException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ExceptionUtilsTest {

    Context context = Mockito.mock(Context.class);
    ValidationException ex = Mockito.mock(ValidationException.class);

    @ParameterizedTest
    @MethodSource("generateMaps")
    void validationMessagesTest(Map<String, List<String>> map) {
        Map<String, List<ValidationError<Object>>> mockMap =
                map.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry ->
                        entry.getValue().stream()
                                .map(message -> new ValidationError<>(message, Map.of("", ""), null))
                                .collect(Collectors.toList())
                ));
        String s = ExceptionUtils.validationMessages(new ValidationException(mockMap));
        assertEquals("<ul><li><b>Key</b>&nbsp;Mess </li></ul>", s);
    }

    @Test
    void isSQLUniqueExceptionTest() {
        Exception ex = new Exception("duplicate key value violates unique constraint");
        assertTrue(ExceptionUtils.isSQLUniqueException(ex));
    }

    @Test
    void isNotSQLUniqueExceptionTest() {
        Exception ex = new Exception("Hello");
        assertFalse(ExceptionUtils.isSQLUniqueException(ex));
    }

    @Test
    void stacktraceTest() {
        Exception ex = new Exception("Some message");
        String s = ExceptionUtils.stacktrace(ex);
        assertNotEquals(null, s);
    }

    @Test
    void wrapErrorNotInstanceofTest() {
        Exception ex = new Exception();
        doNothing().when(context).sessionAttribute(Keys.ERROR_KEY, null);
        ExceptionUtils.wrapError(context, ex);
        verify(context, Mockito.times(1)).sessionAttribute(eq(Keys.ERROR_KEY), eq(null));
    }

    static Stream<Map<String, List<String>>> generateMaps() {
        return Stream.of(Map.of("Key", List.of("Mess")));
    }

    @ParameterizedTest
    @MethodSource("generateMaps")
    void wrapErrorInstanceofTest(Map<String, List<String>> map) {
        Map<String, List<ValidationError<Object>>> mockMap =
                map.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry ->
                        entry.getValue().stream()
                                .map(message -> new ValidationError<>(message, Map.of("", ""), null))
                                .collect(Collectors.toList())
                ));
        doNothing().when(context).sessionAttribute(Keys.ERROR_KEY, null);
        ExceptionUtils.wrapError(context, new ValidationException(mockMap));
        verify(context, Mockito.times(1)).sessionAttribute(eq(Keys.ERROR_KEY), eq("<ul><li><b>Key</b>&nbsp;Mess </li></ul>"));
    }
}