package edu.sumdu.tss.elephant.middleware;


import edu.sumdu.tss.elephant.helper.exception.CheckTokenException;
import io.javalin.http.Context;
import io.javalin.http.util.ContextUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CSRFFilterTest {

    @Mock
    HttpServletRequest request;
    @Mock
    HttpServletResponse response;

    @Test
    @DisplayName("Should generate the token")
    void testGenerate() {
        String mockCSRFToken = "mockCSRFToken";
        Context context = spy(ContextUtil.init(request, response)); // need to setting request
        when(request.getMethod()).thenReturn("GET"); // make the getMethod to return GET

        // make the sessionAttribute to return null
        doReturn(null).when(context).sessionAttribute("SessionID");
        doNothing().when(context).sessionAttribute(anyString(), anyString());

        try (MockedStatic<CSRFTokenService> mockedService = mockStatic(CSRFTokenService.class)) {
            mockedService.when(() -> CSRFTokenService.generateToken(anyString())).thenReturn(mockCSRFToken);

            CSRFFilter.generate(context);

            verify(context).sessionAttribute(eq("csrf"), eq(mockCSRFToken));
        }
    }

    @Test
    @DisplayName("Should generate the null token")
    void testCheckTokenNull() {
        String mockCSRFToken = null;
        Context context = spy(ContextUtil.init(request, response));
        when(request.getMethod()).thenReturn("POST");

        doReturn(null).when(context).header("X-CSRF-TOKEN");
        doReturn(null).when(context).formParam("_csrf");

        doReturn(null).when(context).sessionAttribute("SessionID");

        try (MockedStatic<CSRFTokenService> mockedService = mockStatic(CSRFTokenService.class)) {
            mockedService.when(() -> CSRFTokenService.generateToken(anyString())).thenReturn(mockCSRFToken);

            CSRFFilter.generate(context);

        }

        try (MockedStatic<CSRFTokenService> mockedService = mockStatic(CSRFTokenService.class)) {
            mockedService.when(() -> CSRFTokenService.validateToken(anyString(), anyString())).thenReturn(true);

            assertThrows(CheckTokenException.class, () -> {
                CSRFFilter.check(context);
            });
        }
    }

    @Test
    @DisplayName("Should check the token")
    void testCheck() {
        String mockCSRFToken = "mockCSRFToken";
        Context context = spy(ContextUtil.init(request, response));
        when(request.getMethod()).thenReturn("POST");

        doReturn(mockCSRFToken).when(context).header("X-CSRF-TOKEN");
        doReturn(mockCSRFToken).when(context).formParam("_csrf");

        doReturn(null).when(context).sessionAttribute("SessionID");

        try (MockedStatic<CSRFTokenService> mockedService = mockStatic(CSRFTokenService.class)) {
            mockedService.when(() -> CSRFTokenService.generateToken(anyString())).thenReturn(mockCSRFToken);

            CSRFFilter.generate(context);

        }

        try (MockedStatic<CSRFTokenService> mockedService = mockStatic(CSRFTokenService.class)) {
            mockedService.when(() -> CSRFTokenService.validateToken(anyString(), anyString())).thenReturn(true);

            CSRFFilter.check(context);

        }
    }

}