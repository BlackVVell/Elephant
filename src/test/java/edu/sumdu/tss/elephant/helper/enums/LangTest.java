package edu.sumdu.tss.elephant.helper.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LangTest {

    @Test
    void byValueTest(){
        String lang = "EN";
        assertEquals(Lang.valueOf("EN"), Lang.byValue(lang));
    }

    @Test
    void byValueExTest(){
        String lang = "RU";
        Exception exception = assertThrows(RuntimeException.class, () -> {
            Lang.byValue(lang);
        });
        String expectedMessage = "Language not found for" + lang;
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage,actualMessage);
    }
}
