package edu.sumdu.tss.elephant.helper;

import edu.sumdu.tss.elephant.helper.UserRole;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UserRoleTest {

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void byValue() {
        assertEquals(UserRole.byValue(2).getValue(), 2);
    }

    @Test
    void byNotCorrectValue() {
        assertThrows(RuntimeException.class, () -> {
            UserRole.byValue(10);
        });
    }

    @Test
    void maxConnections() {
    }

    @Test
    void maxDB() {
    }

    @Test
    void maxStorage() {
    }

    @Test
    void maxBackupsPerDB() {
    }

    @Test
    void getValue() {
    }

    @Test
    void values() {
    }

    @Test
    void valueOf() {

    }
}