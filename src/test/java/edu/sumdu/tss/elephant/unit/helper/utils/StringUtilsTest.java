package edu.sumdu.tss.elephant.unit.helper.utils;

import edu.sumdu.tss.elephant.helper.utils.StringUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StringUtilsTest {

    StringUtils stringUtils = new StringUtils();

    @Test
    void randomAlphaStringTest() {
        String s = stringUtils.randomAlphaString(8);
        assertEquals(8, s.length());
    }

    @Test
    void uuid() {
        assertNotNull(stringUtils.uuid());
    }

    @Test
    void replaceLastTest() {
        String s = stringUtils.replaceLast("52839", "28", " ");
        assertEquals("5 39", s);
    }

    @Test
    void notReplaceLastTest() {
        String s = stringUtils.replaceLast("52839", "n", " ");
        assertEquals("52839", s);
    }
}