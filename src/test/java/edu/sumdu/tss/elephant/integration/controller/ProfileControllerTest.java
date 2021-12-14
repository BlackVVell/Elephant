package edu.sumdu.tss.elephant.integration.controller;

import com.icegreen.greenmail.util.DummySSLSocketFactory;
import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetup;
import edu.sumdu.tss.elephant.Server;
import edu.sumdu.tss.elephant.helper.Keys;
import edu.sumdu.tss.elephant.helper.UserRole;
import edu.sumdu.tss.elephant.helper.enums.Lang;
import edu.sumdu.tss.elephant.helper.utils.StringUtils;
import edu.sumdu.tss.elephant.model.User;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

import java.io.File;
import java.security.Security;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProfileControllerTest {

    private static Server server;
    private static Sql2o sql2o;
    private static String FROM_EMAIL;
    private static GreenMail greenMail;
    private final static String EMAIL = "qewr@gmail.com";
    private final static String PASSWORD = "Qgjhgfgvkgvk123@123";

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

        Unirest.post(Keys.get("APP.URL") + "/registration")
                .field("login", EMAIL)
                .field("password", "Qwerty123@").asEmpty();

        Unirest.post(Keys.get("APP.URL") + "/login")
                .field("login", EMAIL)
                .field("password", "Qwerty123@").asString();
        Unirest.get(Keys.get("APP.URL") + "/home").asString();
    }

    @AfterAll
    static void tearDown() {
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



    @Test
    void show() {
        HttpResponse<String> profile = Unirest.get(Keys.get("APP.URL") + "/profile")
                .asString();

        System.out.println(profile.getBody());
        assertNotNull(profile.getBody());
        assertEquals(200,profile.getStatus());
        assertTrue(profile.getBody().contains("Your prefered language is:"));
    }

    @Test
    void language() {
        Unirest.get(Keys.get("APP.URL") + "/profile")
                .asString();

        HttpResponse<String> lang = Unirest.get(Keys.get("APP.URL") + "/profile" + "/lang")
                .queryString("lang", Lang.UK.toString()).asString();

        assertNotNull(lang.getBody());
        assertEquals(200,lang.getStatus());

        try (Connection connection = sql2o.open()) {
            List<User> users =  connection.createQuery("SELECT * FROM USERS WHERE LOGIN = :login " )
                    .addParameter("login" , EMAIL)
                    .executeAndFetch(User.class);
            assertEquals(Lang.UK.toString(), users.get(0).getLanguage());
        }

    }

    @Test
    void resetDbPassword() {

        Unirest.post(Keys.get("APP.URL") +"/profile"+ "/reset-db")
                .field("db-password", PASSWORD)
                .asString();

        try (Connection connection = sql2o.open()) {
            List<User> users =  connection.createQuery("SELECT * FROM USERS WHERE LOGIN = :login " )
                    .addParameter("login" , EMAIL)
                    .executeAndFetch(User.class);
            assertEquals(PASSWORD, users.get(0).getDbPassword());
        }

    }

   /* @Test
    void resetWebPassword() {

        String oldPass;

        try (Connection connection = sql2o.open()) {
            List<User> users =  connection.createQuery("SELECT * FROM USERS WHERE LOGIN = :login " )
                    .addParameter("login" , EMAIL)
                    .executeAndFetch(User.class);

            oldPass = users.get(0).getPassword();
        }

        Unirest.post(Keys.get("APP.URL") +"/reset-password")
                .field("web-password", PASSWORD)
                .asString();

        try (Connection connection = sql2o.open()) {
            List<User> users =  connection.createQuery("SELECT * FROM USERS WHERE LOGIN = :login " )
                    .addParameter("login" , EMAIL)
                    .executeAndFetch(User.class);

            assertNotEquals(oldPass,users.get(0).getPassword());
        }


    }*/

    @Test
    void resetApiPassword() {

        String oldPrivateKey, oldPublicKey;

        try (Connection connection = sql2o.open()) {
            List<User> users =  connection.createQuery("SELECT * FROM USERS WHERE LOGIN = :login " )
                    .addParameter("login" , EMAIL)
                    .executeAndFetch(User.class);

            oldPrivateKey = users.get(0).getPrivateKey();
            oldPublicKey = users.get(0).getPublicKey();
        }

        Unirest.post(Keys.get("APP.URL") + "/profile"+"/reset-api")
                .asString();

        try (Connection connection = sql2o.open()) {
            List<User> users =  connection.createQuery("SELECT * FROM USERS WHERE LOGIN = :login " )
                    .addParameter("login" , EMAIL)
                    .executeAndFetch(User.class);

            assertNotEquals(oldPrivateKey,users.get(0).getPrivateKey());
            assertNotEquals(oldPublicKey,users.get(0).getPublicKey());
        }

    }

    /*@Test
    void upgradeUser() {

        Unirest.post(Keys.get("APP.URL") + "/profile"+"/upgrade")
                .field("role", 2)
                .asString();


        try (Connection connection = sql2o.open()) {
            List<User> users =  connection.createQuery("SELECT * FROM USERS WHERE LOGIN = :login " )
                    .addParameter("login" , EMAIL)
                    .executeAndFetch(User.class);

            assertEquals(2,users.get(0).getRole());
        }

    }*/

    @Test
    void removeSelf() {
    }


}