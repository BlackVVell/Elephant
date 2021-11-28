package edu.sumdu.tss.elephant.helper;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;

import static org.junit.jupiter.api.Assertions.*;

class HmacTest {

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void calculate() throws NoSuchAlgorithmException, InvalidKeyException {
        String data = "test_data";
        String key = "test_key";
        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(), "HmacSHA384");
        Mac mac = Mac.getInstance("HmacSHA384");
        mac.init(secretKeySpec);
        Formatter formatter = new Formatter();
        for (byte b : mac.doFinal(data.getBytes())) {
            formatter.format("%02x", b);
        }
        assertEquals(formatter.toString(), Hmac.calculate(data,key));

    }
}