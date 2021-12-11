package edu.sumdu.tss.elephant.unit.helper.enums;

import edu.sumdu.tss.elephant.helper.enums.Lang;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LangTest {

    @Test
    void byValueTest(){
        String lang = "EN";
        Assertions.assertEquals(Lang.valueOf("EN"), Lang.byValue(lang));
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
