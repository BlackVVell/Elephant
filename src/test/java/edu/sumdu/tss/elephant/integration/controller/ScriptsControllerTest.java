package edu.sumdu.tss.elephant.integration.controller;

import edu.sumdu.tss.elephant.Server;
import edu.sumdu.tss.elephant.helper.Keys;
import edu.sumdu.tss.elephant.helper.UserRole;
import edu.sumdu.tss.elephant.model.*;
import kong.unirest.Unirest;
import org.junit.jupiter.api.*;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ScriptsControllerTest {

    private static Server server;
    private static Sql2o sql2o;

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
        try (Connection connection = sql2o.open()) {
            connection.createQuery("DELETE FROM BACKUPS").executeUpdate();
            connection.createQuery("DELETE FROM DATABASES").executeUpdate();
            connection.createQuery("DELETE FROM LOGGER").executeUpdate();
            connection.createQuery("DELETE FROM SCRIPTS").executeUpdate();
            connection.createQuery("DELETE FROM USERS").executeUpdate();
        }
    }

    @Test
    void test() {
        Unirest.post(Keys.get("APP.URL") + "/registration")
                .field("login", "qewr@gmail.com")
                .field("password", "Qwerty123@").asEmpty();

        Unirest.post(Keys.get("APP.URL") + "/login")
                .field("login", "qewr@gmail.com")
                .field("password", "Qwerty123@").asEmpty();

        Unirest.post(Keys.get("APP.URL") + "/profile/upgrade")
                .field("role", UserRole.BASIC_USER.toString()).asEmpty();

        Unirest.post(Keys.get("APP.URL") + "/database").asEmpty();

        try (Connection connection = sql2o.open()) {
            List<User> users = connection.createQuery("SELECT * FROM USERS WHERE LOGIN = :login")
                    .addParameter("login", "qewr@gmail.com").executeAndFetch(User.class);
            List<Database> databases = DatabaseService.forUser(users.get(0).getUsername());

            Unirest.post(Keys.get("APP.URL") + "/database/{database}/script/")
                    .routeParam("database", databases.get(0).getName())
                    .field("file", new File("script.sql")).asEmpty();

            List<Script> script = ScriptService.list(databases.get(0).getName());

            assertEquals(200, Unirest.get(Keys.get("APP.URL") + "/database/{database}/script/{script}")
                    .routeParam("database", databases.get(0).getName())
                    .routeParam("script", String.valueOf(script.get(0).getId()))
                    .asEmpty().getStatus());

            assertEquals(200, Unirest.get(Keys.get("APP.URL") + "/database/{database}/script/")
                    .routeParam("database", databases.get(0).getName())
                    .asEmpty().getStatus());

            assertEquals(200, Unirest.post(Keys.get("APP.URL") + "/database/{database}/script/{script}")
                    .routeParam("database", databases.get(0).getName())
                    .routeParam("script", String.valueOf(script.get(0).getId()))
                    .asEmpty().getStatus());

            assertEquals(302, Unirest.post(Keys.get("APP.URL") + "/database/{database}/script/{script}/delete")
                    .routeParam("database", databases.get(0).getName())
                    .routeParam("script", String.valueOf(script.get(0).getId()))
                    .asEmpty().getStatus());

            assertEquals(404, Unirest.post(Keys.get("APP.URL") + "/database/{database}/script/{script}/delete")
                    .routeParam("database", databases.get(0).getName())
                    .routeParam("script", String.valueOf(script.get(0).getId()))
                    .asEmpty().getStatus());

            Unirest.post(Keys.get("APP.URL") + "/database/{database}/script/")
                    .routeParam("database", databases.get(0).getName())
                    .field("file", new File("script.sql")).asEmpty();
            Unirest.post(Keys.get("APP.URL") + "/database/{database}/script/")
                    .routeParam("database", databases.get(0).getName())
                    .field("file", new File("script.sql")).asEmpty();
            Unirest.post(Keys.get("APP.URL") + "/database/{database}/script/")
                    .routeParam("database", databases.get(0).getName())
                    .field("file", new File("script.sql")).asEmpty();

        }
    }
}