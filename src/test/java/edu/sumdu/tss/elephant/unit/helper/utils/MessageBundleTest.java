package edu.sumdu.tss.elephant.unit.helper.utils;

import edu.sumdu.tss.elephant.helper.utils.MessageBundle;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MessageBundleTest {


    @Test
    void getTest() {
        MessageBundle mess = new MessageBundle("EN");
        String str = mess.get("mail.conformation");
        assertEquals("Elephant: Welcome to the club buddy", str);
    }

    @Test
    void getErrorTest(){
        MessageBundle mess = new MessageBundle("EN");
        String str = mess.get("mail.conformati");
        assertEquals("I18n not found:mail.conformati", str);
    }

    @Test
    void constructorTest(){
        MessageBundle mess = new MessageBundle("EN");
        assertEquals("Elephant: Welcome to the club buddy",mess.get("mail.conformation"));

    }

    @Test
    void testGet() {
        MessageBundle mess = new MessageBundle("EN");
        String str = mess.get("mail.conformation",1,2,3);
        assertEquals("Elephant: Welcome to the club buddy", str);
    }
}