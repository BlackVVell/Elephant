package edu.sumdu.tss.elephant.integration.model;


import edu.sumdu.tss.elephant.Server;
import edu.sumdu.tss.elephant.helper.Keys;
import edu.sumdu.tss.elephant.helper.UserRole;
import edu.sumdu.tss.elephant.helper.exception.AccessRestrictedException;
import edu.sumdu.tss.elephant.helper.exception.NotFoundException;
import edu.sumdu.tss.elephant.model.Database;
import edu.sumdu.tss.elephant.model.DatabaseService;
import edu.sumdu.tss.elephant.model.User;
import kong.unirest.Unirest;
import org.junit.jupiter.api.*;
import org.sql2o.Connection;
import org.sql2o.Sql2o;


import java.io.File;
import java.util.List;


import static org.junit.jupiter.api.Assertions.*;

public class DatabaseServiceTest {
    private static Server server;
    private static Sql2o sql2o;
    private final static String EMAIL = "qewr@gmail.com";
    private final static String PASS = "Qwerty123@";

    @BeforeAll
    static void setUp() {
        server = new Server();
        Keys.loadParams(new File("config.conf"));
        server.start(Integer.parseInt(Keys.get("APP.PORT")));
        sql2o = new Sql2o("jdbc:postgresql://" + Keys.get("DB.URL") + ":" + Keys.get("DB.PORT") + "/" + Keys.get("DB.NAME"), Keys.get("DB.USERNAME"), Keys.get("DB.PASSWORD"));
    }

    @AfterAll
    static void stopAll() {
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
    void sizeTest(){
        Unirest.post(Keys.get("APP.URL") + "/database").asEmpty();
        try(Connection connection = sql2o.open()) {
            List<User> users = connection.createQuery("SELECT * FROM USERS WHERE LOGIN = :login")
                    .addParameter("login", "qewr@gmail.com").executeAndFetch(User.class);
            assertEquals(1, users.size());

            List<Database> databases = DatabaseService.forUser(users.get(0).getUsername());
            assertEquals(8602115,edu.sumdu.tss.elephant.model.DatabaseService.size(databases.get(0).getName()));
            assertEquals(1, databases.size());
        }
    }

    @Test
    void existsTest () {
        Unirest.post(Keys.get("APP.URL") + "/database").asEmpty();
        try(Connection connection = sql2o.open()) {
            List<User> users = connection.createQuery("SELECT * FROM USERS WHERE LOGIN = :login")
                    .addParameter("login", "qewr@gmail.com").executeAndFetch(User.class);
            assertEquals(1, users.size());

            List<Database> databases = DatabaseService.forUser(users.get(0).getUsername());
            assertTrue(DatabaseService.exists(databases.get(0).getName()));
            assertEquals(1, databases.size());
        }
    }

    @Test
    void activeDatabaseTest() {
        Unirest.post(Keys.get("APP.URL") + "/database").asEmpty();
        try(Connection connection = sql2o.open()) {
            List<User> users = connection.createQuery("SELECT * FROM USERS WHERE LOGIN = :login")
                    .addParameter("login", "qewr@gmail.com").executeAndFetch(User.class);
            assertEquals(1, users.size());

            List<Database> databases = DatabaseService.forUser(users.get(0).getUsername());
            assertEquals("Database(name=" + databases.get(0).getName() + ", owner=" + users.get(0).getUsername() + ")"
                    , DatabaseService.activeDatabase(users.get(0).getUsername(), databases.get(0).getName()).toString());
        }
    }

    @Test
    void activeDatabaseWithExceptionTest() {
        Unirest.post(Keys.get("APP.URL") + "/database").asEmpty();
        try(Connection connection = sql2o.open()) {
            List<User> users = connection.createQuery("SELECT * FROM USERS WHERE LOGIN = :login")
                    .addParameter("login", "qewr@gmail.com").executeAndFetch(User.class);
            assertEquals(1, users.size());

            List<Database> databases = DatabaseService.forUser(users.get(0).getUsername());
            assertThrows(AccessRestrictedException.class, () -> {
                DatabaseService.activeDatabase(null, databases.get(0).getName());
            });
        }
    }

    @Test
    void byNameTest() {
        Unirest.post(Keys.get("APP.URL") + "/database").asEmpty();
        try(Connection connection = sql2o.open()) {
            List<User> users = connection.createQuery("SELECT * FROM USERS WHERE LOGIN = :login")
                    .addParameter("login", "qewr@gmail.com").executeAndFetch(User.class);
            assertEquals(1, users.size());

            List<Database> databases = DatabaseService.forUser(users.get(0).getUsername());
            assertEquals("Database(name=" + databases.get(0).getName() + ", owner=" + users.get(0).getUsername() + ")"
                    , DatabaseService.byName(databases.get(0).getName()).toString());
        }
    }

    @Test
    void byNameWithExceptionTest() {
        Unirest.post(Keys.get("APP.URL") + "/database").asEmpty();
        try(Connection connection = sql2o.open()) {
            List<User> users = connection.createQuery("SELECT * FROM USERS WHERE LOGIN = :login")
                    .addParameter("login", "qewr@gmail.com").executeAndFetch(User.class);
            assertEquals(1, users.size());

            List<Database> databases = DatabaseService.forUser(users.get(0).getUsername());
            assertThrows(NotFoundException.class, () -> {
                DatabaseService.byName(null);
            });
        }
    }

    @Test
    void dropTest() {
        Unirest.post(Keys.get("APP.URL") + "/database").asEmpty();
        try(Connection connection = sql2o.open()) {
            List<User> users = connection.createQuery("SELECT * FROM USERS WHERE LOGIN = :login")
                    .addParameter("login", "qewr@gmail.com").executeAndFetch(User.class);
            assertEquals(1, users.size());

            List<Database> databases = DatabaseService.forUser(users.get(0).getUsername());
            DatabaseService.drop(databases.get(0));
            assertEquals(0, DatabaseService.forUser(users.get(0).getUsername()).size());
        }
    }

    @Test
    void dropWithScriptAndBackupServiceTest() {
        Unirest.post(Keys.get("APP.URL") + "/database").asEmpty();
        try(Connection connection = sql2o.open()) {
            List<User> users = connection.createQuery("SELECT * FROM USERS WHERE LOGIN = :login")
                    .addParameter("login", "qewr@gmail.com").executeAndFetch(User.class);
            assertEquals(1, users.size());
            List<Database> databases = DatabaseService.forUser(users.get(0).getUsername());

            Unirest.post(Keys.get("APP.URL") + "/database/{database}/point")
                    .routeParam("database", databases.get(0).getName())
                    .field("point", "pointName").asString();

            Unirest.post(Keys.get("APP.URL") + "/database/{database}/script")
                    .routeParam("database", databases.get(0).getName())
                    .field("file", new File("script.sql")).asEmpty();

            DatabaseService.drop(databases.get(0));
            assertEquals(0, DatabaseService.forUser(users.get(0).getUsername()).size());
        }
    }
}
