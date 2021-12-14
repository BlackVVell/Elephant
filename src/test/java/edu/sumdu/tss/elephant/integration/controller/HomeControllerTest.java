package edu.sumdu.tss.elephant.integration.controller;

import com.icegreen.greenmail.util.DummySSLSocketFactory;
import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetup;
import edu.sumdu.tss.elephant.Server;
import edu.sumdu.tss.elephant.helper.Keys;
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

import static org.junit.jupiter.api.Assertions.*;

class HomeControllerTest {

    private static Server server;
    private static Sql2o sql2o;
    private static String FROM_EMAIL;
    private static GreenMail greenMail;
    private final static String EMAIL = "qewr@gmail.com";

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

    @BeforeEach
    void logout(){
        Unirest.get(Keys.get("APP.URL") + "/logout").asEmpty();
    }

    @Test
    void show() {
        HttpResponse<String> login = Unirest.post(Keys.get("APP.URL") + "/login")
                .field("login", EMAIL)
                .field("password", "Qwerty123@").asString();

        assertNotNull(login.getBody());
        assertEquals("/home",login.getHeaders().getFirst("Location"));
        assertEquals(302,login.getStatus());

        HttpResponse<String> homeRedirect = Unirest.get(Keys.get("APP.URL") + "/home")
                .asString();
        assertNotNull(homeRedirect.getBody());
        assertTrue(homeRedirect.getBody().contains(EMAIL));
    }

}