package edu.sumdu.tss.elephant.controller;

import edu.sumdu.tss.elephant.Server;
import edu.sumdu.tss.elephant.helper.Keys;
import edu.sumdu.tss.elephant.helper.UserRole;
import edu.sumdu.tss.elephant.model.BackupService;
import edu.sumdu.tss.elephant.model.Database;
import edu.sumdu.tss.elephant.model.DatabaseService;
import edu.sumdu.tss.elephant.model.User;
import kong.unirest.Unirest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseControllerTest {


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
    void dbTest() {
        Unirest.post(Keys.get("APP.URL") + "/registration")
                .field("login", "qewr@gmail.com")
                .field("password", "Qwerty123@").asEmpty();

        Unirest.post(Keys.get("APP.URL") + "/login")
                .field("login", "qewr@gmail.com")
                .field("password", "Qwerty123@").asEmpty();

        Unirest.post(Keys.get("APP.URL") + "/profile/upgrade")
                .field("role", UserRole.ADMIN.toString()).asEmpty();

        String createInsertQuery = "CREATE TABLE test (id INTEGER);\n" +
                "INSERT INTO test VALUES (1);\n";

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
        }
    }

}