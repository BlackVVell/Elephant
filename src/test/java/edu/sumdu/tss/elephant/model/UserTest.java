package edu.sumdu.tss.elephant.model;

import edu.sumdu.tss.elephant.helper.exception.HttpError500;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static edu.sumdu.tss.elephant.helper.UserRole.BASIC_USER;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class UserTest {

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void getPassword() {
        User user = new User();
        String userpassword = user.getPassword();
        assertNull(userpassword);
    }

    @Test
    void setPassword() {
        User user = new User();
        String login = "login";
        String password = "password";
        user.setLogin(login);
        user.setPassword(password);
        String userpassword = user.getPassword();
        assertNotNull(userpassword);
    }

    @Test
    void role() {
        User user = new User();
        long role = 2;
        user.setRole(role);
        assertEquals(user.role(), BASIC_USER);
    }

    @Test
    void crypt() {
        User user = new User();
        String login = "login";
        user.setLogin(login);
        String source = "test string";
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("SHA-384");
        } catch (NoSuchAlgorithmException e) {
            throw new HttpError500("Fail crypt user password", e);
        }
        md.update(login.getBytes());
        byte[] bytes = md.digest();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
        }
        assertEquals(user.crypt(source), sb.toString());
    }

//    @Test
//    //не работает!!!
//    void failCrypt() throws NoSuchAlgorithmException {
//        MessageDigest md = spy(MessageDigest.getInstance(anyString()));
//        when(MessageDigest.getInstance(anyString())).thenReturn(null);
//
//        System.out.println(md.getInstance("sadad"));

//        doReturn(null).when(md).getInstance("SHA-384");
//        User user = new User();
//        String login = "login";
//        user.setLogin(login);
//        String source = "test string";
//        try {
//            md = MessageDigest.getInstance("SHA-384");
//        } catch (NoSuchAlgorithmException e) {
//            throw new HttpError500("Fail crypt user password", e);
//        }
//        md.update(login.getBytes());
//        byte[] bytes = md.digest();
//        StringBuilder sb = new StringBuilder();
//        for (int i = 0; i < bytes.length; i++) {
//            sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
//        }
//        assertEquals(user.crypt(source), sb.toString());
//    }

    @Test
    void resetToken(){
        User user1 = new User();
        user1.resetToken();
        User user2 = new User();
        user2.resetToken();
        assertNotEquals(user1.getToken(), user2.getToken());
    }
}