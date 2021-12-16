package edu.sumdu.tss.elephant.integration.controller;

import com.icegreen.greenmail.util.DummySSLSocketFactory;
import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetup;
import edu.sumdu.tss.elephant.Server;
import edu.sumdu.tss.elephant.helper.Keys;
import edu.sumdu.tss.elephant.helper.UserRole;
import edu.sumdu.tss.elephant.model.*;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.junit.jupiter.api.*;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

import java.io.File;
import java.security.Security;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BackupControllerTest {

    private static Server server;
    private static Sql2o sql2o;
    private static String FROM_EMAIL;
    private static GreenMail greenMail;
    private final static String EMAIL = "qewr@gmail.com";
    private final static String PASS = "Qwerty123@";
    private final static String createInsertQuery = "CREATE TABLE test (id INTEGER);\n" +
            "INSERT INTO test VALUES (1);\n";
    private final static String deleteQuery = "DROP TABLE test;";

    @BeforeAll
    static void setUp() {
        server = new Server();
        Keys.loadParams(new File("config.conf"));
        FROM_EMAIL = Keys.get("EMAIL.FROM");
        server.start(Integer.parseInt(Keys.get("APP.PORT")));
        sql2o = new Sql2o("jdbc:postgresql://" + Keys.get("DB.URL") + ":" + Keys.get("DB.PORT") + "/" + Keys.get("DB.NAME"), Keys.get("DB.USERNAME"), Keys.get("DB.PASSWORD"));

        Security.setProperty("ssl.SocketFactory.provider",
                DummySSLSocketFactory.class.getName());
        greenMail = new GreenMail(new ServerSetup(3025, "127.0.0.1", ServerSetup.PROTOCOL_SMTPS));
        greenMail.setUser(FROM_EMAIL, Keys.get("EMAIL.USER"), Keys.get("EMAIL.PASSWORD"));
        greenMail.start();
    }

    @AfterAll
    static void stopAll() {
        greenMail.stop();
        server.stop();
    }

    @BeforeEach
    void registerLoginAndSetRole() {
        Unirest.post(Keys.get("APP.URL") + "/registration")
                .field("login", EMAIL)
                .field("password", PASS).asEmpty();

        Unirest.post(Keys.get("APP.URL") + "/login")
                .field("login", EMAIL)
                .field("password", PASS).asEmpty();

        Unirest.post(Keys.get("APP.URL") + "/profile/upgrade")
                .field("role", UserRole.BASIC_USER.toString()).asEmpty();
    }

    @AfterEach
    void clear() {
        Unirest.get(Keys.get("APP.URL") + "/logout").asEmpty();

        try (Connection connection = sql2o.open()) {
            connection.createQuery("DELETE FROM BACKUPS").executeUpdate();
            connection.createQuery("DELETE FROM DATABASES").executeUpdate();
            connection.createQuery("DELETE FROM LOGGER").executeUpdate();
            connection.createQuery("DELETE FROM SCRIPTS").executeUpdate();
            connection.createQuery("DELETE FROM USERS").executeUpdate();
        }
    }

    @Test
    void createOneBackupTest() {

        Unirest.post(Keys.get("APP.URL") + "/database/").asEmpty();
        try(Connection connection = sql2o.open()) {
            List<User> users = connection.createQuery("SELECT * FROM USERS WHERE LOGIN = :login")
                    .addParameter("login", "qewr@gmail.com").executeAndFetch(User.class);
            assertEquals(1, users.size());

            List<Database> databases = DatabaseService.forUser(users.get(0).getUsername());
            assertEquals(1, databases.size());
            Unirest.post(Keys.get("APP.URL") + "/database/{database}/sql")
                    .routeParam("database", databases.get(0).getName())
                    .field("query", createInsertQuery)
                    .asString();

            Unirest.post(Keys.get("APP.URL") + "/database/{database}/point")
                    .routeParam("database", databases.get(0).getName())
                    .field("point", "pointName").asString();

            assertEquals(1, BackupService.list(databases.get(0).getName()).size());
        }
    }

    @Test
    void createMaxBackupTest() {

        Unirest.post(Keys.get("APP.URL") + "/database").asEmpty();
        try (Connection connection = sql2o.open()) {
            List<User> users = connection.createQuery("SELECT * FROM USERS WHERE LOGIN = :login")
                    .addParameter("login", "qewr@gmail.com").executeAndFetch(User.class);
            assertEquals(1, users.size());

            List<Database> databases = DatabaseService.forUser(users.get(0).getUsername());
            assertEquals(1, databases.size());
            Unirest.post(Keys.get("APP.URL") + "/database/{database}/sql")
                    .routeParam("database", databases.get(0).getName())
                    .field("query", createInsertQuery)
                    .asString();

            Unirest.post(Keys.get("APP.URL") + "/database/{database}/point")
                    .routeParam("database", databases.get(0).getName())
                    .field("point", "pointName").asString();

            Unirest.post(Keys.get("APP.URL") + "/database/{database}/sql")
                    .routeParam("database", databases.get(0).getName())
                    .field("query", deleteQuery)
                    .asString();

            Unirest.post(Keys.get("APP.URL") + "/database/{database}/point")
                    .routeParam("database", databases.get(0).getName())
                    .field("point", "pointName1").asString();

            Unirest.post(Keys.get("APP.URL") + "/database/{database}/sql")
                    .routeParam("database", databases.get(0).getName())
                    .field("query", createInsertQuery)
                    .asString();

            Unirest.post(Keys.get("APP.URL") + "/database/{database}/point")
                    .routeParam("database", databases.get(0).getName())
                    .field("point", "pointName2").asString();
            assertEquals(1, BackupService.list(databases.get(0).getName()).size());
        }
    }

    @Test
    void createPointIsNullTest() {

        Unirest.post(Keys.get("APP.URL") + "/database").asEmpty();
        try (Connection connection = sql2o.open()) {
            List<User> users = connection.createQuery("SELECT * FROM USERS WHERE LOGIN = :login")
                    .addParameter("login", "qewr@gmail.com").executeAndFetch(User.class);
            assertEquals(1, users.size());

            List<Database> databases = DatabaseService.forUser(users.get(0).getUsername());
            assertEquals(1, databases.size());
            Unirest.post(Keys.get("APP.URL") + "/database/{database}/sql")
                    .routeParam("database", databases.get(0).getName())
                    .field("query", createInsertQuery)
                    .asString();

            Unirest.post(Keys.get("APP.URL") + "/database/{database}/point")
                    .routeParam("database", databases.get(0).getName())
                    .asString();

            assertEquals(0, BackupService.list(databases.get(0).getName()).size());
        }
    }

    @Test
    void indexTest() {

        Unirest.post(Keys.get("APP.URL") + "/database").asEmpty();
        try(Connection connection = sql2o.open()) {
            List<User> users = connection.createQuery("SELECT * FROM USERS WHERE LOGIN = :login")
                    .addParameter("login", "qewr@gmail.com").executeAndFetch(User.class);
            assertEquals(1, users.size());

            List<Database> databases = DatabaseService.forUser(users.get(0).getUsername());
            assertEquals(1, databases.size());
            Unirest.post(Keys.get("APP.URL") + "/database/{database}/sql")
                    .routeParam("database", databases.get(0).getName())
                    .field("query", createInsertQuery)
                    .asString();

            Unirest.post(Keys.get("APP.URL") + "/database/{database}/point")
                    .routeParam("database", databases.get(0).getName())
                    .field("point", "pointName").asString();

            HttpResponse<String> point = Unirest.get(Keys.get("APP.URL") + "/database/{database}/point")
                    .routeParam("database", databases.get(0).getName())
                    .asString();

            assertNotNull(point.getBody());
            assertEquals(200, point.getStatus());
        }
    }

    @Test
    void deleteTest() {

        Unirest.post(Keys.get("APP.URL") + "/database").asEmpty();
        try(Connection connection = sql2o.open()) {
            List<User> users = connection.createQuery("SELECT * FROM USERS WHERE LOGIN = :login")
                    .addParameter("login", "qewr@gmail.com").executeAndFetch(User.class);
            assertEquals(1, users.size());

            List<Database> databases = DatabaseService.forUser(users.get(0).getUsername());
            assertEquals(1, databases.size());
            Unirest.post(Keys.get("APP.URL") + "/database/{database}/sql")
                    .routeParam("database", databases.get(0).getName())
                    .field("query", createInsertQuery)
                    .asString();

            Unirest.post(Keys.get("APP.URL") + "/database/{database}/point")
                    .routeParam("database", databases.get(0).getName())
                    .field("point", "pointName").asString();

            assertEquals(1, BackupService.list(databases.get(0).getName()).size());

            List<Backup> backups = BackupService.list(databases.get(0).getName());
            assertEquals("pointName", backups.get(0).getPoint());

            Unirest.post(Keys.get("APP.URL") + "/database/{database}/point/{point}/delete")
                    .routeParam("database", databases.get(0).getName())
                    .routeParam("point", backups.get(0).getPoint())
                    .asString();

            assertEquals(0, BackupService.list(databases.get(0).getName()).size());
        }
    }

    @Test
    void resetTest() {

        Unirest.post(Keys.get("APP.URL") + "/database").asEmpty();
        try(Connection connection = sql2o.open()) {
            List<User> users = connection.createQuery("SELECT * FROM USERS WHERE LOGIN = :login")
                    .addParameter("login", "qewr@gmail.com").executeAndFetch(User.class);
            assertEquals(1, users.size());

            List<Database> databases = DatabaseService.forUser(users.get(0).getUsername());
            assertEquals(1, databases.size());

            Unirest.post(Keys.get("APP.URL") + "/database/{database}/sql")
                    .routeParam("database", databases.get(0).getName())
                    .field("query", createInsertQuery)
                    .asString();

            Unirest.post(Keys.get("APP.URL") + "/database/{database}/point")
                    .routeParam("database", databases.get(0).getName())
                    .field("point", "pointName").asString();

            assertEquals(1, BackupService.list(databases.get(0).getName()).size());

            List<Backup> backups = BackupService.list(databases.get(0).getName());
            assertEquals(1, databases.size());

            Unirest.post(Keys.get("APP.URL") + "/database/{database}/sql")
                    .routeParam("database", databases.get(0).getName())
                    .field("query", deleteQuery)
                    .asString();


            Unirest.post(Keys.get("APP.URL") + "/database/{database}/point/{point}/reset")
                    .routeParam("database", databases.get(0).getName())
                    .field("point", "pointName")
                    .routeParam("point", backups.get(0).getPoint())
                    .asString();
            assertEquals(1, backups.size());
        }
    }
}