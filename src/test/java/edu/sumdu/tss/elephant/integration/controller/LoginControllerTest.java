package edu.sumdu.tss.elephant.integration.controller;

import com.icegreen.greenmail.store.FolderException;
import com.icegreen.greenmail.util.DummySSLSocketFactory;
import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetup;
import edu.sumdu.tss.elephant.Server;
import edu.sumdu.tss.elephant.helper.Keys;
import edu.sumdu.tss.elephant.helper.enums.Lang;
import edu.sumdu.tss.elephant.model.User;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import kong.unirest.UnirestException;
import org.junit.jupiter.api.*;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.File;
import java.io.IOException;
import java.security.Security;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LoginControllerTest {

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
    }

    @AfterAll
    static void tearDown() {
        greenMail.stop();
        server.stop();
    }

    @BeforeEach
    void logout(){
        Unirest.post(Keys.get("APP.URL") + "/registration")
                .field("login", EMAIL)
                .field("password", "Qwerty123@").asEmpty();

        Unirest.get(Keys.get("APP.URL") + "/logout").asEmpty();
    }

    @AfterEach
    void clear(){
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
    void show() {
        HttpResponse<String> response = Unirest.get(Keys.get("APP.URL") + "/login")
                .asString();

        assertNotNull(response.getBody());
        assertEquals(200,response.getStatus());
        assertTrue(response.getBody().contains("Forgot password?"));
    }

    @Test
    void createValidTest() {

        HttpResponse<String> login = Unirest.post(Keys.get("APP.URL") + "/login")
                .field("login", EMAIL)
                .field("password", "Qwerty123@").asString();

        assertNotNull(login.getBody());
        assertEquals("/home",login.getHeaders().getFirst("Location"));
        assertEquals(302,login.getStatus());

        HttpResponse<String> redirect = Unirest.get(Keys.get("APP.URL") +
                        login.getHeaders().getFirst("Location")).asString();

        assertEquals(200,redirect.getStatus());
        assertNotNull(redirect.getBody());
        assertTrue(redirect.getBody().contains("Check your mail and push mail-verification link - to be able create new database."));
    }

    @Test
    void createNotEmailTest() {
        HttpResponse<String> login = Unirest.post(Keys.get("APP.URL") + "/login")
                .field("login", "not" + EMAIL)
                .field("password", "Qwerty123@").asString();

        assertEquals("/login",login.getHeaders().getFirst("Location"));
        assertEquals(302,login.getStatus());

        HttpResponse<String> redirect = Unirest.get(Keys.get("APP.URL") +
                        login.getHeaders().getFirst("Location")).asString();

        assertNotNull(redirect.getBody());
        assertEquals(200,redirect.getStatus());
        assertTrue(redirect.getBody().contains("User or password not known"));
    }

    @Test
    void createNotValidEmailTest() {
        HttpResponse<String> login = Unirest.post(Keys.get("APP.URL") + "/login")
                .field("login", "notvalid")
                .field("password", "Qwerty123").asString();

        assertEquals("/login",login.getHeaders().getFirst("Location"));
        assertEquals(302,login.getStatus());

        HttpResponse<String> redirect = Unirest.get(Keys.get("APP.URL") +
                login.getHeaders().getFirst("Location")).asString();

        assertNotNull(redirect.getBody());
        assertEquals(200,redirect.getStatus());
        assertTrue(redirect.getBody().contains("User or password not known"));

    }

    @Test
    void resetLink() throws MessagingException, FolderException {
        greenMail.purgeEmailFromAllMailboxes();

        HttpResponse<String> reset = Unirest.post(Keys.get("APP.URL") + "/login/reset-password")
                .field("email", EMAIL).asString();

        System.out.println(reset.getBody());
        assertEquals("/login",reset.getHeaders().getFirst("Location"));
        assertEquals(302,reset.getStatus());

        HttpResponse<String> redirect = Unirest.get(Keys.get("APP.URL") +
                        reset.getHeaders().getFirst("Location")).asString();

        assertNotNull(redirect.getBody());
        assertEquals(200,redirect.getStatus());
        assertTrue(redirect.getBody().contains("Mail with conformation link was sent. Check yor mailbox."));

        MimeMessage[] messages = greenMail.getReceivedMessagesForDomain(EMAIL);
        Message message = messages[0];
        assertEquals(1, messages.length);

        assertEquals("Elephant: Reset password",message.getSubject());
    }

    @Test
    void resetLinkNotEmail() throws MessagingException, FolderException {
        greenMail.purgeEmailFromAllMailboxes();

        HttpResponse<String> reset = Unirest.post(Keys.get("APP.URL") + "/login/reset-password")
                .field("email", "not" + EMAIL).asString();

        assertNotNull(reset.getBody());
        assertEquals(200,reset.getStatus());
        assertTrue(reset.getBody().contains("User or password not known"));
    }

    @Test
    void resetLinkNotValidEmail() throws MessagingException, FolderException {
        greenMail.purgeEmailFromAllMailboxes();

        HttpResponse<String> reset = Unirest.post(Keys.get("APP.URL") + "/login/reset-password")
                .field("email", "not").asString();

        assertNotNull(reset.getBody());
        assertEquals(200,reset.getStatus());
        assertTrue(reset.getBody().contains("Is it a valid mail?"));
    }

    @Test
    void resetPassword() throws MessagingException, IOException, FolderException {
        greenMail.purgeEmailFromAllMailboxes();

        HttpResponse<String> reset = Unirest.post(Keys.get("APP.URL") + "/login/reset-password")
                .field("email", EMAIL).asString();

        HttpResponse<String> redirect = Unirest.get(Keys.get("APP.URL") +
                        reset.getHeaders().getFirst("Location")).asString();

        MimeMessage[] messages = greenMail.getReceivedMessagesForDomain(EMAIL);
        Message message = messages[0];
        assertEquals(1, messages.length);

        MimeMultipart content = (MimeMultipart) message.getContent();
        String emailMessage = content.getBodyPart(0).getContent().toString();

        int start = emailMessage.indexOf("http");
        int end = emailMessage.indexOf(System.lineSeparator(), start) - 1;

        String url = emailMessage.substring(start,end);

        int startLink = url.lastIndexOf("?") + 1;
        int startToken = url.lastIndexOf("=") + 1;
        String token = url.substring(startToken);
        String getUrl = url.substring(0, startLink);

        HttpResponse<String> send = Unirest.get(getUrl)
                .queryString("token", token).asString();

        assertNotNull(send.getBody());
        assertEquals(200,send.getStatus());
        assertTrue(send.getBody().contains("Chose new password"));

        HttpResponse<String> changePassword = Unirest.post(Keys.get("APP.URL") + "/login/reset")
                .field("token", token)
                .field("password", "Qwerty123@Q").asString();

        assertEquals(302, changePassword.getStatus());
        assertEquals("/login",changePassword.getHeaders().getFirst("Location"));

        HttpResponse<String> login = Unirest.get(Keys.get("APP.URL") + changePassword.getHeaders().getFirst("Location"))
                .asString();

        assertNotNull(login.getBody());
        assertEquals(200,login.getStatus());
        assertTrue(login.getBody().contains("Password has been reset"));

        try(Connection connection = sql2o.open()) {
            List<User> users = connection.createQuery("SELECT * FROM USERS WHERE LOGIN = :login")
                    .addParameter("login", EMAIL).executeAndFetch(User.class);
            assertEquals(1, users.size());
            assertNotEquals(token,users.get(0).getToken());
        }

    }

    @Test
    void resetPasswordNotValidToken() throws MessagingException, IOException, FolderException {
        greenMail.purgeEmailFromAllMailboxes();

        HttpResponse<String> reset = Unirest.post(Keys.get("APP.URL") + "/login/reset-password")
                .field("email", EMAIL).asString();

        HttpResponse<String> redirect = Unirest.get(Keys.get("APP.URL") +
                        reset.getHeaders().getFirst("Location")).asString();

        MimeMessage[] messages = greenMail.getReceivedMessagesForDomain(EMAIL);
        Message message = messages[0];
        assertEquals(1, messages.length);

        MimeMultipart content = (MimeMultipart) message.getContent();
        String emailMessage = content.getBodyPart(0).getContent().toString();

        int start = emailMessage.indexOf("http");
        int end = emailMessage.indexOf(System.lineSeparator(), start) - 1;

        String url = emailMessage.substring(start,end);

        int startLink = url.lastIndexOf("?") + 1;
        int startToken = url.lastIndexOf("=") + 1;
        String token = url.substring(startToken);
        String getUrl = url.substring(0, startLink);

        HttpResponse<String> send = Unirest.get(getUrl)
                .queryString("token", token).asString();

        assertNotNull(send.getBody());
        assertEquals(200,send.getStatus());
        assertTrue(send.getBody().contains("Chose new password"));

        HttpResponse<String> changePassword = Unirest.post(Keys.get("APP.URL") + "/login/reset")
                .field("token", token + "qwerty")
                .field("password", "Qwerty123@Q").asString();

        assertEquals(302, changePassword.getStatus());
        assertEquals("/login",changePassword.getHeaders().getFirst("Location"));
        assertTrue(changePassword.getBody().contains("Unknown or invalid token"));
    }

    @Test
    void resetNotValidPassword() throws MessagingException, IOException, FolderException {
        greenMail.purgeEmailFromAllMailboxes();

        HttpResponse<String> reset = Unirest.post(Keys.get("APP.URL") + "/login/reset-password")
                .field("email", EMAIL).asString();

        HttpResponse<String> redirect = Unirest.get(Keys.get("APP.URL") +
                reset.getHeaders().getFirst("Location")).asString();

        MimeMessage[] messages = greenMail.getReceivedMessagesForDomain(EMAIL);
        Message message = messages[0];
        assertEquals(1, messages.length);

        MimeMultipart content = (MimeMultipart) message.getContent();
        String emailMessage = content.getBodyPart(0).getContent().toString();

        int start = emailMessage.indexOf("http");
        int end = emailMessage.indexOf(System.lineSeparator(), start) - 1;

        String url = emailMessage.substring(start,end);

        int startLink = url.lastIndexOf("?") + 1;
        int startToken = url.lastIndexOf("=") + 1;
        String token = url.substring(startToken);
        String getUrl = url.substring(0, startLink);

        HttpResponse<String> send = Unirest.get(getUrl)
                .queryString("token", token).asString();

        assertNotNull(send.getBody());
        assertEquals(200,send.getStatus());
        assertTrue(send.getBody().contains("Chose new password"));

        HttpResponse<String> changePassword = Unirest.post(Keys.get("APP.URL") + "/login/reset")
                .field("token", token)
                .field("password", "Qwerty12345").asString();

        System.out.println(changePassword.getBody());
        assertNotNull(changePassword.getBody());
        assertEquals(200, changePassword.getStatus());
        assertTrue(changePassword.getBody().contains("I18n not found:validation.invalid.empty"));
    }

    @Test
    void destroy() {
        HttpResponse<String> login = Unirest.post(Keys.get("APP.URL") + "/login")
                .field("login", EMAIL)
                .field("password", "Qwerty123@").asString();

        assertNotNull(login.getBody());
        assertEquals("/home",login.getHeaders().getFirst("Location"));
        assertEquals(302,login.getStatus());

        HttpResponse<String> redirect = Unirest.get(Keys.get("APP.URL") +
                        login.getHeaders().getFirst("Location")).asString();

        assertEquals(200,redirect.getStatus());
        assertNotNull(redirect.getBody());

        HttpResponse<String> destroy = Unirest.get(Keys.get("APP.URL") + "/logout").asString();

        assertNotNull(destroy.getBody());
        assertEquals(200,destroy.getStatus());
        assertTrue(destroy.getBody().contains("Forgot password?"));
    }

    @Test
    void langUK() {
        HttpResponse<String> response = Unirest.get(Keys.get("APP.URL") + "/login").asString();

        assertNotNull(response.getBody());
        assertEquals(200,response.getStatus());
        assertTrue(response.getBody().contains("Forgot password?"));

        HttpResponse<String> language = Unirest.get(Keys.get("APP.URL") + "/login/lang/{lang}")
                .routeParam("lang", Lang.UK.toString()).asString();

        assertNotNull(language.getBody());
        assertEquals(200,language.getStatus());
        assertTrue(language.getBody().contains("Натиснувши Увійти, ви погоджуєтеся з умовами використання."));

        Unirest.get(Keys.get("APP.URL") + "/login/lang/{lang}")
                .routeParam("lang", Lang.EN.toString()).asEmpty();
    }

    @Test
    void langEN() {
        HttpResponse<String> response = Unirest.get(Keys.get("APP.URL") + "/login").asString();

        assertNotNull(response.getBody());
        assertEquals(200,response.getStatus());
        assertTrue(response.getBody().contains("Forgot password?"));

        HttpResponse<String> language = Unirest.get(Keys.get("APP.URL") + "/login/lang/{lang}")
                .routeParam("lang", Lang.EN.toString()).asString();

        assertNotNull(language.getBody());
        assertEquals(200,language.getStatus());
        assertTrue(language.getBody().contains("Forgot password?"));

    }

    @Test
    void langNot() throws UnirestException {
        HttpResponse<String> response = Unirest.get(Keys.get("APP.URL") + "/login").asString();

        assertNotNull(response.getBody());
        assertEquals(200,response.getStatus());
        assertTrue(response.getBody().contains("Forgot password?"));


        HttpResponse<String> language = Unirest.get(Keys.get("APP.URL") + "/login/lang/{lang}")
                .routeParam("lang", "PL").asString();

        assertNotNull(language.getBody());
        assertEquals(200,language.getStatus());
        assertTrue(language.getBody().contains("Forgot password?"));
    }
}