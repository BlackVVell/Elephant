package edu.sumdu.tss.elephant.unit.helper.utils;

import edu.sumdu.tss.elephant.helper.utils.ParameterizedStringFactory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ParameterizedStringFactoryTest {

    public static String test = "select :test from :table";

    @Test
    void addParameter() {
        ParameterizedStringFactory instance = new ParameterizedStringFactory(test);
        String actual = instance.addParameter("test", "TEST").addParameter("table", "TABLE").toString();
        assertEquals(actual, "select TEST from TABLE");
    }

    @Test
    void testToString() {
        ParameterizedStringFactory instance = new ParameterizedStringFactory(test);
        assertEquals(test, instance.toString());
    }
}