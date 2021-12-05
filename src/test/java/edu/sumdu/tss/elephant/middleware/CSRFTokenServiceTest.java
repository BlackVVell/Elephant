package edu.sumdu.tss.elephant.middleware;

import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import javax.annotation.processing.ProcessingEnvironment;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CSRFTokenServiceTest {


    @Test
    void getSiteWideTokenNotNull() {
        CSRFTokenService.getSiteWideToken();
    }

    @Test
    void generateToken() {
        String sessionID = "test_value";
        String value = CSRFTokenService.generateToken(sessionID);
        String[] str = value.split("-");
        String data = sessionID + str[1];
        HashFunction hash = Hashing.hmacSha256(CSRFTokenService.getSiteWideToken().getBytes(StandardCharsets.UTF_8));
        String token = hash.hashString(data, StandardCharsets.UTF_8).toString();
        assertEquals(token + '-' + str[1], value, "token generated");
    }

    @Test
    void getSiteWideTokenIsNull() throws NoSuchFieldException, IllegalAccessException {
//        MockedStatic<CSRFTokenService> mockedService = mockStatic(CSRFTokenService.class);
        CSRFTokenService csrfTokenService = new CSRFTokenService();
        Field privateField = CSRFTokenService.class.getDeclaredField("siteWideToken");
        privateField.setAccessible(true);
        privateField.set(csrfTokenService, null);
        assertThrows(RuntimeException.class, () -> {
                    CSRFTokenService.getSiteWideToken();
                });
        privateField.set(csrfTokenService, "test token");
//        String fieldValue = (String) privateField.get(csrfTokenService);
//        System.out.println("fieldValue = " + fieldValue);
    }

//    @Test
//    void getSiteWideTokenIsNull2() throws NoSuchFieldException, IllegalAccessException {

//        CSRFTokenService csrfTokenService = new CSRFTokenService();
//        Field privateField = CSRFTokenService.class.getDeclaredField("siteWideToken");
//        privateField.setAccessible(true);
//        privateField.set(csrfTokenService, null);
//
//
//
//        CSRFTokenService.getSiteWideToken();
//
//        privateField.set(csrfTokenService, "test token");

//    }

    @Test
    void validateToken() {
        String sessionID = "test_value";
        String value = CSRFTokenService.generateToken(sessionID);
        String[] str = value.split("-");
        String data = sessionID + str[1];
        HashFunction hash = Hashing.hmacSha256(CSRFTokenService.getSiteWideToken().getBytes(StandardCharsets.UTF_8));
        String token = hash.hashString(data, StandardCharsets.UTF_8).toString();
        assertTrue(CSRFTokenService.validateToken(token + '-' + str[1], sessionID));
    }
}