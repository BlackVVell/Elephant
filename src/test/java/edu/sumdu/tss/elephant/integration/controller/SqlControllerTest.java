package edu.sumdu.tss.elephant.integration.controller;

import com.icegreen.greenmail.util.DummySSLSocketFactory;
import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetup;
import edu.sumdu.tss.elephant.Server;
import edu.sumdu.tss.elephant.controller.SqlController;
import edu.sumdu.tss.elephant.helper.DBPool;
import edu.sumdu.tss.elephant.helper.Keys;
import edu.sumdu.tss.elephant.helper.UserRole;
import edu.sumdu.tss.elephant.helper.enums.Lang;
import edu.sumdu.tss.elephant.helper.utils.StringUtils;
import edu.sumdu.tss.elephant.model.Database;
import edu.sumdu.tss.elephant.model.Log;
import edu.sumdu.tss.elephant.model.User;
import edu.sumdu.tss.elephant.model.UserService;
import io.javalin.http.HandlerType;
import io.javalin.http.util.ContextUtil;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.junit.jupiter.api.*;

import io.javalin.http.Context;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.security.Security;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

public class SqlControllerTest {

    private static Server server;
    private static Sql2o sql2o;
    private final static String EMAIL = "qewr@gmail.com";

    @BeforeAll
    static void setUp() {
        server = new Server();
        Keys.loadParams(new File("config.conf"));
        server.start(Integer.parseInt(Keys.get("APP.PORT")));
        sql2o = new Sql2o("jdbc:postgresql://" + Keys.get("DB.URL") + ":" + Keys.get("DB.PORT") + "/" + Keys.get("DB.NAME"), Keys.get("DB.USERNAME"), Keys.get("DB.PASSWORD"));

        Unirest.post(Keys.get("APP.URL") + "/registration")
                .field("login", EMAIL)
                .field("password", "Qwerty123@").asEmpty();

    }

    @AfterEach
    void clearDatabses() {
        try (Connection connection = sql2o.open()) {
            connection.createQuery("DELETE FROM DATABASES").executeUpdate();
        }
    }

    @BeforeEach
    void login() {
        Unirest.post(Keys.get("APP.URL") + "/login")
                .field("login", EMAIL)
                .field("password", "Qwerty123@").asString();
        Unirest.get(Keys.get("APP.URL") + "/home").asString();

        Unirest.get(Keys.get("APP.URL") + "/profile").asEmpty();

        Unirest.post(Keys.get("APP.URL") + "/profile" + "/upgrade")
                .field("role", UserRole.BASIC_USER)
                .asEmpty();

        Unirest.post(Keys.get("APP.URL") + "/database").asEmpty();
    }

    @AfterAll
    static void tearDown() {
        Unirest.get(Keys.get("APP.URL") + "/logout")
                .asEmpty();
        server.stop();
        try (Connection connection = sql2o.open()) {
            connection.createQuery("DELETE FROM BACKUPS").executeUpdate();
            connection.createQuery("DELETE FROM DATABASES").executeUpdate();
            connection.createQuery("DELETE FROM LOGGER").executeUpdate();
            connection.createQuery("DELETE FROM SCRIPTS").executeUpdate();
            connection.createQuery("DELETE FROM USERS").executeUpdate();
        }
    }


    @Test
    @DisplayName("Should show /database/{database}/sql")
    void showTest() {
        String databaseName;

        try (Connection connection = sql2o.open()) {
            List<Database> databases = connection.createQuery("SELECT * FROM DATABASES")
                    .executeAndFetch(Database.class);
            assertEquals(1, databases.size());
            databaseName = databases.get(0).getName();
        }

        HttpResponse<String> sqlShow = Unirest.get(Keys.get("APP.URL") + "/database/" + databaseName + "/sql").asString();

        assertNotNull(sqlShow.getBody());
        assertEquals(200, sqlShow.getStatus());
        assertTrue(sqlShow.getBody().contains("Run"));

        Unirest.get(Keys.get("APP.URL") + "/logout")
                .asEmpty();

    }

    @Test
    @DisplayName("Should run valid sql response")
    void runTest() {
        String databaseName;

        try (Connection connection = sql2o.open()) {
            List<Database> databases = connection.createQuery("SELECT * FROM DATABASES")
                    .executeAndFetch(Database.class);
            assertEquals(1, databases.size());
            databaseName = databases.get(0).getName();
        }

        HttpResponse<String> sqlShow = Unirest.post(Keys.get("APP.URL") + "/database/" + databaseName + "/sql")
                .field("query", """
                        CREATE TABLE films (
                            code        char(5),
                            title       varchar(40),
                            did         integer,
                            date_prod   date,
                            kind        varchar(10),
                            len         interval hour to minute
                        );""").asString();

        assertNotNull(sqlShow.getBody());
        assertEquals(200, sqlShow.getStatus());

        HttpResponse<String> sqlInsert = Unirest.post(Keys.get("APP.URL") + "/database/" + databaseName + "/sql")
                .field("query", """
                        do $$
                                        begin
                                        for r in 1..302 loop
                                        INSERT INTO films (code, title, did, date_prod, kind) VALUES
                                                                     ('q', 'Tampopo', 110, '1985-02-10', 'Comedy');
                                        end loop;
                                        end;
                                        $$;""").asString();

        HttpResponse<String> showFilms = Unirest.post(Keys.get("APP.URL") + "/database/" + databaseName + "/sql")
                .field("query", "SELECT * FROM FILMS").asString();

        assertNotNull(showFilms.getBody());
        assertEquals(200, showFilms.getStatus());

        System.out.println(showFilms.getBody());

        HttpResponse<String> showFilm = Unirest.post(Keys.get("APP.URL") + "/database/" + databaseName + "/sql")
                .field("query", "SELECT * FROM FILM").asString();

        assertNotNull(showFilm.getBody());
        assertEquals(200, showFilm.getStatus());
        assertTrue(showFilms.getBody().contains("kind"));
        assertTrue(showFilms.getBody().contains("300+ (extra rows omitted)"));

        HttpResponse<String> showUpdate = Unirest.post(Keys.get("APP.URL") + "/database/" + databaseName + "/sql")
                .field("query", "UPDATE films SET kind = 'Dramatic' WHERE kind = 'Comedy';").asString();

        assertNotNull(showUpdate.getBody());
        assertEquals(200, showUpdate.getStatus());

        HttpResponse<String> showSearch = Unirest.post(Keys.get("APP.URL") + "/database/" + databaseName + "/sql")
                .field("query", "SELECT * WHERE ;").asString();


        Unirest.get(Keys.get("APP.URL") + "/logout")
                .asEmpty();
    }

    @Test
    @DisplayName("Should run null sql response")
    void runNullTest() {
        String databaseName;

        try (Connection connection = sql2o.open()) {
            List<Database> databases = connection.createQuery("SELECT * FROM DATABASES")
                    .executeAndFetch(Database.class);
            assertEquals(1, databases.size());
            databaseName = databases.get(0).getName();
        }

        HttpResponse<String> showFilms = Unirest.post(Keys.get("APP.URL") + "/database/" + databaseName + "/sql")
                .field("query", "").asString();

        assertNotNull(showFilms.getBody());
        assertEquals(200, showFilms.getStatus());

        Unirest.get(Keys.get("APP.URL") + "/logout")
                .asEmpty();
    }
}