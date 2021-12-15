package edu.sumdu.tss.elephant.integration.controller;

import com.icegreen.greenmail.store.FolderException;
import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetup;
import edu.sumdu.tss.elephant.Server;
import edu.sumdu.tss.elephant.helper.Keys;
import edu.sumdu.tss.elephant.helper.UserRole;
import edu.sumdu.tss.elephant.model.User;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.junit.jupiter.api.*;
import org.junit.platform.commons.util.StringUtils;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RegistrationControllerTest {

    private static Server server;
    private static Sql2o sql2o;
    private static GreenMail greenMail;
    private static String FROM_EMAIL;
    final String EMAIL = "qewr@gmail.com";

    @BeforeAll
    static void setUp() {
        server = new Server();
        Keys.loadParams(new File("config.conf"));
        FROM_EMAIL = Keys.get("EMAIL.FROM");
        server.start(Integer.parseInt(Keys.get("APP.PORT")));
        sql2o = new Sql2o("jdbc:postgresql://" + Keys.get("DB.URL") + ":" + Keys.get("DB.PORT") + "/" + Keys.get("DB.NAME"), Keys.get("DB.USERNAME"), Keys.get("DB.PASSWORD"));

        greenMail = new GreenMail(new ServerSetup(456,"127.0.0.1",ServerSetup.PROTOCOL_SMTP));
        greenMail.setUser(FROM_EMAIL, Keys.get("EMAIL.USER"), Keys.get("EMAIL.PASSWORD"));
        greenMail.start();
    }

    @BeforeEach
    void clearDb() throws FolderException {
        greenMail.purgeEmailFromAllMailboxes();
        try (Connection connection = sql2o.open()) {
            connection.createQuery("DELETE FROM BACKUPS").executeUpdate();
            connection.createQuery("DELETE FROM DATABASES").executeUpdate();
            connection.createQuery("DELETE FROM LOGGER").executeUpdate();
            connection.createQuery("DELETE FROM SCRIPTS").executeUpdate();
            connection.createQuery("DELETE FROM USERS").executeUpdate();
        }
    }

    @AfterAll
    static void tearDown() {
        greenMail.stop();
        server.stop();
    }

    @Test
    void show() {
        HttpResponse<String> response = Unirest.get(Keys.get("APP.URL") + "/registration")
                .asString();

        assertTrue(response.getBody().contains("Password conformation"));
        assertNotNull(response.getBody());
        assertEquals(200,response.getStatus());
    }

    @Test
    void createValidDate() throws MessagingException {
        HttpResponse<String> response = Unirest.post(Keys.get("APP.URL") + "/registration")
                .field("login", EMAIL)
                .field("password", "Qwerty123@").asString();

        assertNotNull(response.getBody());
        assertEquals("/home",response.getHeaders().getFirst("Location"));
        assertEquals(302,response.getStatus());

        HttpResponse<String> redirect = Unirest.get(Keys.get("APP.URL") +
                        response.getHeaders().getFirst("Location"))
                .asString();

        MimeMessage[] messages = greenMail.getReceivedMessagesForDomain(EMAIL);
        Message message = messages[0];

        assertEquals(1, messages.length);
        assertEquals(FROM_EMAIL, message.getFrom()[0].toString());
        assertEquals("Elephant: Welcome to the club buddy", message.getSubject());

        assertNotNull(redirect.getBody());
        assertTrue(redirect.getBody().contains("Check your mail and push mail-verification link - to be able create new database."));
        assertEquals(200,redirect.getStatus());

        try(Connection connection = sql2o.open()) {
            List<User> users = connection.createQuery("SELECT * FROM USERS WHERE LOGIN = :login")
                    .addParameter("login", EMAIL).executeAndFetch(User.class);
            assertEquals(1, users.size());
            assertEquals(EMAIL, users.get(0).getLogin());
            assertEquals("EN", users.get(0).getLanguage());
            assertEquals(UserRole.UNCHEKED.getValue(), users.get(0).getRole());
            assertTrue(StringUtils.isNotBlank(users.get(0).getPassword()));
            assertTrue(StringUtils.isNotBlank(users.get(0).getToken()));
            assertTrue(StringUtils.isNotBlank(users.get(0).getUsername()));
            assertTrue(StringUtils.isNotBlank(users.get(0).getPrivateKey()));
            assertTrue(StringUtils.isNotBlank(users.get(0).getPublicKey()));
            assertTrue(StringUtils.isNotBlank(users.get(0).getDbPassword()));

            Path path = Paths.get("/var/run/postgresql/" + users.get(0).getUsername());
            assertTrue(Files.exists(path));

            Path tablespace = Paths.get("/var/run/postgresql/" + users.get(0).getUsername() + "/tablespace");
            assertTrue(Files.exists(tablespace));

        }

        Unirest.get(Keys.get("APP.URL") + "/logout").asEmpty();

    }

    @Test
    void createNotValidDate() throws MessagingException {
        HttpResponse<String> response = Unirest.post(Keys.get("APP.URL") + "/registration")
                .field("login", EMAIL)
                .field("password", "Qwerty123@").asString();

        assertNotNull(response.getBody());
        assertEquals("/home",response.getHeaders().getFirst("Location"));
        assertEquals(302,response.getStatus());

        HttpResponse<String> redirect = Unirest.get(Keys.get("APP.URL") +
                        response.getHeaders().getFirst("Location"))
                .asString();

        MimeMessage[] messages = greenMail.getReceivedMessagesForDomain(EMAIL);
        Message message = messages[0];

        assertEquals(1, messages.length);
        assertEquals(FROM_EMAIL, message.getFrom()[0].toString());
        assertEquals("Elephant: Welcome to the club buddy", message.getSubject());

        assertNotNull(redirect.getBody());
        assertTrue(redirect.getBody().contains("Check your mail and push mail-verification link - to be able create new database."));
        assertEquals(200,redirect.getStatus());

        try(Connection connection = sql2o.open()) {
            List<User> users = connection.createQuery("SELECT * FROM USERS WHERE LOGIN = :login")
                    .addParameter("login", EMAIL).executeAndFetch(User.class);
            assertEquals(1, users.size());
            assertEquals(EMAIL, users.get(0).getLogin());
            assertEquals("EN", users.get(0).getLanguage());
            assertEquals(UserRole.UNCHEKED.getValue(), users.get(0).getRole());
            assertTrue(StringUtils.isNotBlank(users.get(0).getPassword()));
            assertTrue(StringUtils.isNotBlank(users.get(0).getToken()));
            assertTrue(StringUtils.isNotBlank(users.get(0).getUsername()));
            assertTrue(StringUtils.isNotBlank(users.get(0).getPrivateKey()));
            assertTrue(StringUtils.isNotBlank(users.get(0).getPublicKey()));
            assertTrue(StringUtils.isNotBlank(users.get(0).getDbPassword()));

            Path path = Paths.get("/var/run/postgresql/" + users.get(0).getUsername());
            assertTrue(Files.exists(path));

            Path tablespace = Paths.get("/var/run/postgresql/" + users.get(0).getUsername() + "/tablespace");
            assertTrue(Files.exists(tablespace));

        }

        Unirest.get(Keys.get("APP.URL") + "/logout").asEmpty();

        HttpResponse<String> againReg = Unirest.post(Keys.get("APP.URL") + "/registration")
                .field("login", EMAIL)
                .field("password", "Qwerty123@").asString();

        assertEquals("/registration",againReg.getHeaders().getFirst("Location"));

        HttpResponse<String> redirectToReg = Unirest.get(Keys.get("APP.URL") +
                        againReg.getHeaders().getFirst("Location"))
                .asString();

        assertNotNull(redirectToReg.getBody());
        assertEquals(200,redirectToReg.getStatus());
        assertTrue(redirectToReg.getBody().contains("Login (email) already taken"));

    }

    @Test
    void createNotValidPass() throws MessagingException {
        HttpResponse<String> response = Unirest.post(Keys.get("APP.URL") + "/registration")
                .field("login", EMAIL)
                .field("password", "Qwerty123").asString();

        assertNotNull(response.getBody());
        assertEquals("/registration",response.getHeaders().getFirst("Location"));
        assertEquals(302,response.getStatus());

        HttpResponse<String> redirect = Unirest.get(Keys.get("APP.URL") +
                        response.getHeaders().getFirst("Location"))
                .asString();

        assertEquals(200,redirect.getStatus());
        assertTrue(redirect.getBody().contains("Password should be at least 8 symbols,  " +
                "with at least 1 digit, 1 uppercase letter and 1 non alpha-num symbol"));
    }

    @Test
    void userConformation() throws MessagingException, FolderException, IOException {
        HttpResponse<String> response = Unirest.post(Keys.get("APP.URL") + "/registration")
                .field("login", EMAIL)
                .field("password", "Qwerty123@").asString();

        assertNotNull(response.getBody());
        assertEquals("/home",response.getHeaders().getFirst("Location"));
        assertEquals(302,response.getStatus());

        HttpResponse<String> redirect = Unirest.get(Keys.get("APP.URL") +
                        response.getHeaders().getFirst("Location"))
                .asString();

        MimeMessage[] messages = greenMail.getReceivedMessagesForDomain(EMAIL);
        Message message = messages[0];

        assertEquals(1, messages.length);
        assertEquals(FROM_EMAIL, message.getFrom()[0].toString());
        assertEquals("Elephant: Welcome to the club buddy", message.getSubject());

        greenMail.purgeEmailFromAllMailboxes();

        assertNotNull(redirect.getBody());
        assertTrue(redirect.getBody().contains("Check your mail and push mail-verification link - to be able create new database."));
        assertEquals(200,redirect.getStatus());

        try(Connection connection = sql2o.open()) {
            List<User> users = connection.createQuery("SELECT * FROM USERS WHERE LOGIN = :login")
                    .addParameter("login", EMAIL).executeAndFetch(User.class);
            assertEquals(1, users.size());
            assertEquals(EMAIL, users.get(0).getLogin());
            assertEquals("EN", users.get(0).getLanguage());
            assertEquals(UserRole.UNCHEKED.getValue(), users.get(0).getRole());
            assertTrue(StringUtils.isNotBlank(users.get(0).getPassword()));
            assertTrue(StringUtils.isNotBlank(users.get(0).getToken()));
            assertTrue(StringUtils.isNotBlank(users.get(0).getUsername()));
            assertTrue(StringUtils.isNotBlank(users.get(0).getPrivateKey()));
            assertTrue(StringUtils.isNotBlank(users.get(0).getPublicKey()));
            assertTrue(StringUtils.isNotBlank(users.get(0).getDbPassword()));

            Path path = Paths.get("/var/run/postgresql/" + users.get(0).getUsername());
            assertTrue(Files.exists(path));

            Path tablespace = Paths.get("/var/run/postgresql/" + users.get(0).getUsername() + "/tablespace");
            assertTrue(Files.exists(tablespace));

        }

        HttpResponse<String> resendMail = Unirest.get(Keys.get("APP.URL") + "/registration" + "/resend-confirm")
                .asString();

        assertEquals(1, messages.length);
        assertEquals(FROM_EMAIL, message.getFrom()[0].toString());
        assertEquals("Elephant: Welcome to the club buddy", message.getSubject());

        MimeMultipart content = (MimeMultipart) message.getContent();
        String emailMessage = content.getBodyPart(0).getContent().toString();

        int start = emailMessage.indexOf("http");
        int end = emailMessage.indexOf(System.lineSeparator(), start) - 1;

        String url = emailMessage.substring(start,end);

        int startToken = url.lastIndexOf("/") + 1;
        String token = url.substring(startToken);
//        String urlToken = url.substring(0, startToken) + "{token}";
        System.out.println(token);


        assertEquals(Keys.get("APP.URL") + "/registration/confirm/" + token, url);

        assertNotNull(resendMail.getBody());
        assertEquals(200,resendMail.getStatus());
        assertTrue(resendMail.getBody().contains("Resend conformation email"));

        Unirest.get(Keys.get("APP.URL") + "/logout").asEmpty();

    }

    @Test
    void resendUserConformation() throws FolderException, MessagingException, IOException {
        HttpResponse<String> response = Unirest.post(Keys.get("APP.URL") + "/registration")
                .field("login", EMAIL)
                .field("password", "Qwerty123@").asString();

        assertNotNull(response.getBody());
        assertEquals("/home",response.getHeaders().getFirst("Location"));
        assertEquals(302,response.getStatus());

        HttpResponse<String> redirect = Unirest.get(Keys.get("APP.URL") +
                        response.getHeaders().getFirst("Location"))
                .asString();

        MimeMessage[] messages = greenMail.getReceivedMessagesForDomain(EMAIL);
        Message message = messages[0];

        assertEquals(1, messages.length);
        assertEquals(FROM_EMAIL, message.getFrom()[0].toString());
        assertEquals("Elephant: Welcome to the club buddy", message.getSubject());

        greenMail.purgeEmailFromAllMailboxes();

        assertNotNull(redirect.getBody());
        assertTrue(redirect.getBody().contains("Check your mail and push mail-verification link - to be able create new database."));
        assertEquals(200,redirect.getStatus());

        try(Connection connection = sql2o.open()) {
            List<User> users = connection.createQuery("SELECT * FROM USERS WHERE LOGIN = :login")
                    .addParameter("login", EMAIL).executeAndFetch(User.class);
            assertEquals(1, users.size());
            assertEquals(EMAIL, users.get(0).getLogin());
            assertEquals("EN", users.get(0).getLanguage());
            assertEquals(UserRole.UNCHEKED.getValue(), users.get(0).getRole());
            assertTrue(StringUtils.isNotBlank(users.get(0).getPassword()));
            assertTrue(StringUtils.isNotBlank(users.get(0).getToken()));
            assertTrue(StringUtils.isNotBlank(users.get(0).getUsername()));
            assertTrue(StringUtils.isNotBlank(users.get(0).getPrivateKey()));
            assertTrue(StringUtils.isNotBlank(users.get(0).getPublicKey()));
            assertTrue(StringUtils.isNotBlank(users.get(0).getDbPassword()));

            Path path = Paths.get("/var/run/postgresql/" + users.get(0).getUsername());
            assertTrue(Files.exists(path));

            Path tablespace = Paths.get("/var/run/postgresql/" + users.get(0).getUsername() + "/tablespace");
            assertTrue(Files.exists(tablespace));

        }

        HttpResponse<String> resendMail = Unirest.get(Keys.get("APP.URL") + "/registration" + "/resend-confirm")
                .asString();

        assertEquals(1, messages.length);
        assertEquals(FROM_EMAIL, message.getFrom()[0].toString());
        assertEquals("Elephant: Welcome to the club buddy", message.getSubject());

        MimeMultipart content = (MimeMultipart) message.getContent();
        String emailMessage = content.getBodyPart(0).getContent().toString();

        int start = emailMessage.indexOf("http");
        int end = emailMessage.indexOf(System.lineSeparator(), start) - 1;

        String url = emailMessage.substring(start,end);

        int startToken = url.lastIndexOf("/") + 1;
        String token = url.substring(startToken);
        String getUrl = url.substring(0, startToken) + "{token}";

        assertEquals(Keys.get("APP.URL") + "/registration/confirm/" + token, url);

        assertNotNull(resendMail.getBody());
        assertEquals(200,resendMail.getStatus());
        assertTrue(resendMail.getBody().contains("Resend conformation email"));


        HttpResponse<String> send = Unirest.get(getUrl)
                .routeParam("token" , token)
                .asString();

        assertEquals(200, send.getStatus());
        assertTrue(send.getBody().contains("Email approved"));

        Unirest.get(Keys.get("APP.URL") + "/logout").asEmpty();
    }

    @Test
    void userConformationNotToken(){
        HttpResponse<String> send = Unirest.get(Keys.get("APP.URL") + "/registration/confirm/{token}")
                .routeParam("token" , "qwerrty")
                .asString();

        assertEquals(404, send.getStatus());
        assertTrue(send.getBody().contains("User with token qwerrty not found"));
    }

}