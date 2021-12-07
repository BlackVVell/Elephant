package edu.sumdu.tss.elephant.helper;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mockStatic;

class DBPoolTest {

    @BeforeEach
    void setUp() {
        Keys.loadParams(new File("config.conf"));
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void getConnectionNotArgument() {
        Object Sql2o = null;
        String name = Keys.get("DB.NAME");
        DBPool.getConnection();

        try (MockedStatic<DBPool> mockedStatic = mockStatic(DBPool.class)) {
            mockedStatic.when(DBPool::getConnection).thenReturn(null);

            assertEquals(Sql2o, DBPool.getConnection());
            mockedStatic.verify(DBPool::getConnection);
        }

        DBPool.getConnection();
    }

    @Test
    void getConnection() {
        Object Sql2o = null;
        String name = Keys.get("DB.NAME");
        DBPool.getConnection(name);

        try (MockedStatic<DBPool> mockedStatic = mockStatic(DBPool.class)) {
            mockedStatic.when(() -> DBPool.getConnection(anyString())).thenReturn(null);

            assertEquals(Sql2o, DBPool.getConnection(name));
            mockedStatic.verify(() -> DBPool.getConnection(name));
        }

        DBPool.getConnection(name);
        assertNotNull(DBPool.getConnection(name).open());
    }

    @Test
    void dbUtilUrl() {
        String nameDB = "test";
        String url = DBPool.dbUtilUrl(nameDB);
        String expected = String.format("postgresql://%s:%s@%s:%s/%s", Keys.get("DB.USERNAME"), Keys.get("DB.PASSWORD"), Keys.get("DB.URL"), Keys.get("DB.PORT"), nameDB);

        assertEquals(expected, url);
    }
}