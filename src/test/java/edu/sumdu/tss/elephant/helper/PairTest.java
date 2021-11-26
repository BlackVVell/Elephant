package edu.sumdu.tss.elephant.helper;

import edu.sumdu.tss.elephant.helper.Pair;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class PairTest {

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void getKey() {
        String key = "a";
        String value = "b";
        Pair pair = new Pair(key,value);
        assertEquals(pair.getKey(),key);
    }

    @Test
    void getValue() {
        String key = "a";
        String value = "b";
        Pair pair = new Pair(key,value);
        assertEquals(pair.getValue(),value);
    }

    @Test
    void getValueEmpty() {
        Pair pair = new Pair();
        assertNull(pair.getValue());
    }

    @Test
    void getKeyEmpty() {
        Pair pair = new Pair();
        assertNull(pair.getKey());
    }
}