package edu.sumdu.tss.elephant.integration.controller.api;

import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetup;
import edu.sumdu.tss.elephant.Server;
import edu.sumdu.tss.elephant.helper.Hmac;
import edu.sumdu.tss.elephant.helper.Keys;
import edu.sumdu.tss.elephant.helper.UserRole;
import edu.sumdu.tss.elephant.model.Backup;
import edu.sumdu.tss.elephant.model.Database;
import edu.sumdu.tss.elephant.model.User;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import org.junit.jupiter.api.*;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

import java.io.File;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ApiControllerTest {

    private static Server server;
    private static Sql2o sql2o;
    private static String FROM_EMAIL;
    private static GreenMail greenMail;

    private final static String EMAIL = "qewr@gmail.com";

    @BeforeAll
    static void setUp() {
        server = new Server();
        Keys.loadParams(new File("config.conf"));
        server.start(Integer.parseInt(Keys.get("APP.PORT")));
        sql2o = new Sql2o("jdbc:postgresql://" + Keys.get("DB.URL") + ":" + Keys.get("DB.PORT") + "/" + Keys.get("DB.NAME"), Keys.get("DB.USERNAME"), Keys.get("DB.PASSWORD"));

        FROM_EMAIL = Keys.get("EMAIL.FROM");
        greenMail = new GreenMail(new ServerSetup(456, "127.0.0.1", ServerSetup.PROTOCOL_SMTP));
        greenMail.setUser(FROM_EMAIL, Keys.get("EMAIL.USER"), Keys.get("EMAIL.PASSWORD"));
        greenMail.start();

        Unirest.post(Keys.get("APP.URL") + "/registration")
                .field("login", EMAIL)
                .field("password", "Qwerty123@").asEmpty();
        Unirest.get(Keys.get("APP.URL") + "/home").asEmpty();

        Unirest.post(Keys.get("APP.URL") + "/profile"+"/upgrade")
                .field("role", UserRole.PROMOTED_USER)
                .asString();

        Unirest.post(Keys.get("APP.URL") + "/database/")
                .asString();
    }

    @AfterAll
    static void tearDown() {
        Unirest.get(Keys.get("APP.URL") + "/logout")
                .asEmpty();

        greenMail.stop();
        server.stop();
        try (Connection connection = sql2o.open()) {
            connection.createQuery("DELETE FROM BACKUPS").executeUpdate();
            connection.createQuery("DELETE FROM DATABASES").executeUpdate();
            connection.createQuery("DELETE FROM LOGGER").executeUpdate();
            connection.createQuery("DELETE FROM SCRIPTS").executeUpdate();
            connection.createQuery("DELETE FROM USERS").executeUpdate();
        }
    }

    @AfterEach
    void resetBackups(){
        try (Connection connection = sql2o.open()) {
            connection.createQuery("DELETE FROM BACKUPS").executeUpdate();
        }
    }


    @Test
    @DisplayName("Should be not created backup can't validate user")
    void backupNotValidUser() {
        String databaseName = null;
        String publicKey = null;
        String checkedHmac = null;
        String backupName = "qwerty";

        try(Connection connection = sql2o.open()) {
            List<User> users = connection.createQuery("SELECT * FROM USERS WHERE LOGIN = :login")
                    .addParameter("login", EMAIL).executeAndFetch(User.class);
            assertEquals(1, users.size());

            String name = users.get(0).getUsername();
            publicKey = users.get(0).getPublicKey();

            List<Database> databases = connection.createQuery("SELECT * FROM DATABASES WHERE OWNER = :owner")
                    .addParameter("owner",name).executeAndFetch(Database.class);
            assertEquals(1, databases.size());

            databaseName = databases.get(0).getName();
            checkedHmac = Hmac.calculate("/api/v1/database/"+ databaseName +"/create/" + backupName, users.get(0).getPrivateKey());
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            e.printStackTrace();
        }


        HttpResponse<String> apiCreate = Unirest.post(Keys.get("APP.URL") + "/api/v1/database/{name}/create/{point}")
                .body("")
                .header("publickey", publicKey)
                .header("signature", checkedHmac+"qwe")
                .routeParam("name", databaseName)
                .routeParam("point",backupName)
                .asString();

        assertEquals(200,apiCreate.getStatus());
        assertTrue(apiCreate.getBody().contains("Can't validate user"));

        try(Connection connection = sql2o.open()) {
            List<Backup> backups = connection.createQuery("SELECT * FROM BACKUPS WHERE DATABASE = :database")
                    .addParameter("database", databaseName).executeAndFetch(Backup.class);
            assertEquals(0, backups.size());
        }
    }

    @Test
    @DisplayName("Should be created backup")
    void backup() {
        String databaseName = null;
        String publicKey = null;
        String checkedHmac = null;
        String backupName = "qwerty";

        try(Connection connection = sql2o.open()) {
            List<User> users = connection.createQuery("SELECT * FROM USERS WHERE LOGIN = :login")
                    .addParameter("login", EMAIL).executeAndFetch(User.class);
            assertEquals(1, users.size());

            String name = users.get(0).getUsername();
            publicKey = users.get(0).getPublicKey();

            List<Database> databases = connection.createQuery("SELECT * FROM DATABASES WHERE OWNER = :owner")
                    .addParameter("owner",name).executeAndFetch(Database.class);
            assertEquals(1, databases.size());

            databaseName = databases.get(0).getName();
            checkedHmac = Hmac.calculate("/api/v1/database/"+ databaseName +"/create/" + backupName, users.get(0).getPrivateKey());
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            e.printStackTrace();
        }


        HttpResponse<String> apiCreate = Unirest.post(Keys.get("APP.URL") + "/api/v1/database/{name}/create/{point}")
                .body("")
                .header("publickey", publicKey)
                .header("signature", checkedHmac)
                .routeParam("name", databaseName)
                .routeParam("point",backupName)
                .asString();

        assertEquals(204,apiCreate.getStatus());

        try(Connection connection = sql2o.open()) {
            List<Backup> backups = connection.createQuery("SELECT * FROM BACKUPS WHERE DATABASE = :database")
                    .addParameter("database", databaseName).executeAndFetch(Backup.class);
            assertEquals(1, backups.size());
            assertEquals(backupName,backups.get(0).getPoint());

        }
    }

    @Test
    @DisplayName("Should be reset backup")
    void restore() throws InterruptedException {
        String databaseName = null;
        String publicKey = null;
        String checkedHmac = null;
        String backupName = "resettest";

        try(Connection connection = sql2o.open()) {
            List<User> users = connection.createQuery("SELECT * FROM USERS WHERE LOGIN = :login")
                    .addParameter("login", EMAIL).executeAndFetch(User.class);
            assertEquals(1, users.size());

            String name = users.get(0).getUsername();
            publicKey = users.get(0).getPublicKey();

            List<Database> databases = connection.createQuery("SELECT * FROM DATABASES WHERE OWNER = :owner")
                    .addParameter("owner",name).executeAndFetch(Database.class);
            assertEquals(1, databases.size());

            databaseName = databases.get(0).getName();

            checkedHmac = Hmac.calculate("/api/v1/database/"+ databaseName +"/reset/" + backupName, users.get(0).getPrivateKey());
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            e.printStackTrace();
        }

        Unirest.post(Keys.get("APP.URL") + "/database/"+ databaseName +"/point/{point}/create")
                .routeParam("point", "any" + backupName).asEmpty();
        Unirest.post(Keys.get("APP.URL") + "/database/"+ databaseName +"/point/{point}/create")
                .routeParam("point", backupName).asEmpty();

        Unirest.post(Keys.get("APP.URL") + "/database/"+ databaseName +"/point/{point}/reset")
                .routeParam("point", "any" + backupName).asEmpty();

        Thread.sleep(1000);

        try(Connection connection = sql2o.open()) {
            List<Backup> backupOne = connection.createQuery("SELECT * FROM BACKUPS WHERE POINT = :point")
                    .addParameter("point", "any" + backupName).executeAndFetch(Backup.class);
            assertEquals(1, backupOne.size());

            List<Backup> backupTwo = connection.createQuery("SELECT * FROM BACKUPS WHERE POINT = :point")
                    .addParameter("point", backupName).executeAndFetch(Backup.class);
            assertEquals(1, backupTwo.size());

            Date prev = backupOne.get(0).getUpdatedAt();
            Date curr= backupTwo.get(0).getUpdatedAt();

            assertTrue(prev.after(curr));
        }

        HttpResponse<JsonNode> apiReset = Unirest.post(Keys.get("APP.URL") + "/api/v1/database/{name}/reset/{point}")
                .body("")
                .header("publickey", publicKey)
                .header("signature", checkedHmac)
                .routeParam("name", databaseName)
                .routeParam("point",backupName)
                .asJson();

        assertEquals(204,apiReset.getStatus());

        try(Connection connection = sql2o.open()) {
            List<Backup> backupOne = connection.createQuery("SELECT * FROM BACKUPS WHERE POINT = :point")
                    .addParameter("point", "any" + backupName).executeAndFetch(Backup.class);
            assertEquals(1, backupOne.size());

            List<Backup> backupTwo = connection.createQuery("SELECT * FROM BACKUPS WHERE POINT = :point")
                    .addParameter("point", backupName).executeAndFetch(Backup.class);
            assertEquals(1, backupTwo.size());

            Date prev = backupOne.get(0).getUpdatedAt();
            Date curr= backupTwo.get(0).getUpdatedAt();

            assertTrue(prev.before(curr));
        }

    }

    @Test
    @DisplayName("Should be not reset backup not valid")
    void restoreNotValid() throws InterruptedException {
        String databaseName = null;
        String publicKey = null;
        String checkedHmac = null;
        String backupName = "resettest";

        try(Connection connection = sql2o.open()) {
            List<User> users = connection.createQuery("SELECT * FROM USERS WHERE LOGIN = :login")
                    .addParameter("login", EMAIL).executeAndFetch(User.class);
            assertEquals(1, users.size());

            String name = users.get(0).getUsername();
            publicKey = users.get(0).getPublicKey();

            List<Database> databases = connection.createQuery("SELECT * FROM DATABASES WHERE OWNER = :owner")
                    .addParameter("owner",name).executeAndFetch(Database.class);
            assertEquals(1, databases.size());

            databaseName = databases.get(0).getName();

            checkedHmac = Hmac.calculate("/api/v1/database/"+ databaseName +"/reset/" + backupName, users.get(0).getPrivateKey());
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            e.printStackTrace();
        }

        Unirest.post(Keys.get("APP.URL") + "/database/"+ databaseName +"/point/{point}/create")
                .routeParam("point", "any" + backupName).asEmpty();
        Unirest.post(Keys.get("APP.URL") + "/database/"+ databaseName +"/point/{point}/create")
                .routeParam("point", backupName).asEmpty();

        Unirest.post(Keys.get("APP.URL") + "/database/"+ databaseName +"/point/{point}/reset")
                .routeParam("point", "any" + backupName).asEmpty();

        Thread.sleep(1000);

        try(Connection connection = sql2o.open()) {
            List<Backup> backupOne = connection.createQuery("SELECT * FROM BACKUPS WHERE POINT = :point")
                    .addParameter("point", "any" + backupName).executeAndFetch(Backup.class);
            assertEquals(1, backupOne.size());

            List<Backup> backupTwo = connection.createQuery("SELECT * FROM BACKUPS WHERE POINT = :point")
                    .addParameter("point", backupName).executeAndFetch(Backup.class);
            assertEquals(1, backupTwo.size());

            Date prev = backupOne.get(0).getUpdatedAt();
            Date curr= backupTwo.get(0).getUpdatedAt();

            assertTrue(prev.after(curr));
        }

        HttpResponse<String> apiReset = Unirest.post(Keys.get("APP.URL") + "/api/v1/database/{name}/reset/{point}")
                .body("")
                .header("publickey", publicKey)
                .header("signature", checkedHmac + "q")
                .routeParam("name", databaseName)
                .routeParam("point",backupName)
                .asString();

        assertEquals(200,apiReset.getStatus());
        assertTrue(apiReset.getBody().contains("Can't validate user"));

    }
}