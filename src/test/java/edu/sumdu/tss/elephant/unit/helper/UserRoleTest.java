package edu.sumdu.tss.elephant.unit.helper;

import edu.sumdu.tss.elephant.helper.UserRole;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
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
        Assertions.assertEquals(UserRole.byValue(2).getValue(), 2);
    }

    @Test
    void byNotCorrectValue() {
        assertThrows(RuntimeException.class, () -> {
            UserRole.byValue(10);
        });
    }

    @Test
    void maxConnections() {
        assertEquals(UserRole.ANYONE.maxConnections(), 0);
        assertEquals(UserRole.UNCHEKED.maxConnections(), 0);
        assertEquals(UserRole.BASIC_USER.maxConnections(), 5);
        assertEquals(UserRole.PROMOTED_USER.maxConnections(), 5);
        assertEquals(UserRole.ADMIN.maxConnections(), 5);
    }

    @Test
    void maxDB() {
        assertEquals(UserRole.ANYONE.maxDB(), 0);
        assertEquals(UserRole.UNCHEKED.maxDB(), 0);
        assertEquals(UserRole.BASIC_USER.maxDB(), 2);
        assertEquals(UserRole.PROMOTED_USER.maxDB(), 3);
        assertEquals(UserRole.ADMIN.maxDB(), 100);
    }

    @Test
    void maxStorage() {
        assertEquals(UserRole.ANYONE.maxStorage(), 0);
        assertEquals(UserRole.UNCHEKED.maxStorage(), 0);
        assertEquals(UserRole.BASIC_USER.maxStorage(), 20 * FileUtils.ONE_MB);
        assertEquals(UserRole.PROMOTED_USER.maxStorage(), 50 * FileUtils.ONE_MB);
        assertEquals(UserRole.ADMIN.maxStorage(), 50 * FileUtils.ONE_MB);
    }

    @Test
    void maxBackupsPerDB() {
        assertEquals(UserRole.ANYONE.maxBackupsPerDB(), 0);
        assertEquals(UserRole.UNCHEKED.maxBackupsPerDB(), 0);
        assertEquals(UserRole.BASIC_USER.maxBackupsPerDB(), 1);
        assertEquals(UserRole.PROMOTED_USER.maxBackupsPerDB(), 5);
        assertEquals(UserRole.ADMIN.maxBackupsPerDB(), 10);
    }

    @Test
    void maxScriptsPerDB() {
        assertEquals(UserRole.ANYONE.maxScriptsPerDB(), 0);
        assertEquals(UserRole.UNCHEKED.maxScriptsPerDB(), 0);
        assertEquals(UserRole.BASIC_USER.maxScriptsPerDB(), 2);
        assertEquals(UserRole.PROMOTED_USER.maxScriptsPerDB(), 5);
        assertEquals(UserRole.ADMIN.maxScriptsPerDB(), 10);
    }
}