package edu.sumdu.tss.elephant.E2E;

import com.icegreen.greenmail.store.FolderException;
import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetup;
import edu.sumdu.tss.elephant.helper.Keys;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MailLanguageTest {

    private WebDriver driver;
    private static Sql2o sql2o;
    private static GreenMail greenMail;
    private static String FROM_EMAIL;
    final String EMAIL = "qwer@gmail.com";

    @BeforeAll
    static void setUp() {
        Keys.loadParams(new File("config.conf"));
        FROM_EMAIL = Keys.get("EMAIL.FROM");
        sql2o = new Sql2o("jdbc:postgresql://" + Keys.get("DB.URL") + ":" + Keys.get("DB.PORT") + "/" + Keys.get("DB.NAME"), Keys.get("DB.USERNAME"), Keys.get("DB.PASSWORD"));

        greenMail = new GreenMail(new ServerSetup(456,"127.0.0.1",ServerSetup.PROTOCOL_SMTP));
        greenMail.start();
    }

    @BeforeEach
    void clearDb() throws FolderException {
        driver = new ChromeDriver();
    }

    @AfterEach
    void driverQuit() throws FolderException {
        driver.quit();
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
    }

    @Test
    void registrationENTest() throws MessagingException, IOException {
        greenMail.setUser(FROM_EMAIL, Keys.get("EMAIL.USER"), Keys.get("EMAIL.PASSWORD"));
        driver.get("http://127.0.0.1:7000/login");
        driver.findElement(By.linkText("Sign Up")).click();
        driver.findElement(By.id("email")).click();
        driver.findElement(By.id("email")).sendKeys("qwer@gmail.com");
        driver.findElement(By.id("password")).click();
        driver.findElement(By.id("password")).sendKeys("Qwerty123@");
        driver.findElement(By.id("conformation")).sendKeys("Qwerty123@");
        driver.findElement(By.cssSelector(".w-100")).click();
        driver.findElement(By.linkText("Logout")).click();
        MimeMessage[] messages = greenMail.getReceivedMessagesForDomain(EMAIL);
        Message message = messages[0];

        MimeMultipart content = (MimeMultipart) message.getContent();
        String emailMessage = content.getBodyPart(0).getContent().toString();

        int s = emailMessage.indexOf("(if");
        String actualText = emailMessage.substring(s);
        String expectedText =  "(if you did not register then just ignore this message)";

        assertEquals(expectedText, actualText);
    }

    @Test
    void registrationUKTest() throws MessagingException, IOException {
        greenMail.setUser(FROM_EMAIL, Keys.get("EMAIL.USER"), Keys.get("EMAIL.PASSWORD"));
        driver.get("http://127.0.0.1:7000/login");
        driver.findElement(By.linkText("\uD83C\uDDEC\uD83C\uDDE7")).click();
        driver.findElement(By.linkText("🇺🇦")).click();
        driver.findElement(By.linkText("Зареєструватися")).click();
        driver.findElement(By.id("email")).click();
        driver.findElement(By.id("email")).sendKeys("qwer@gmail.com");
        driver.findElement(By.id("password")).click();
        driver.findElement(By.id("password")).sendKeys("Qwerty123@");
        driver.findElement(By.id("conformation")).sendKeys("Qwerty123@");
        driver.findElement(By.cssSelector(".w-100")).click();
        driver.findElement(By.linkText("Вийти")).click();
        MimeMessage[] messages = greenMail.getReceivedMessagesForDomain(EMAIL);
        Message message = messages[0];

        MimeMultipart content = (MimeMultipart) message.getContent();
        String emailMessage = content.getBodyPart(0).getContent().toString();

        int s = emailMessage.indexOf("(Якщо");
        String actualText = emailMessage.substring(s);
        String expectedText =  "(Якщо ви не реєструвалися проігноруйте це повідомлення)";

        assertEquals(expectedText, actualText);
    }

    @Test
    void resendMailENTest() throws MessagingException, IOException {
        driver.get("http://127.0.0.1:7000/login");
        driver.findElement(By.linkText("Sign Up")).click();
        driver.findElement(By.id("email")).click();
        driver.findElement(By.id("email")).sendKeys("qwer@gmail.com");
        driver.findElement(By.id("password")).click();
        driver.findElement(By.id("password")).sendKeys("Qwerty123@");
        driver.findElement(By.id("conformation")).sendKeys("Qwerty123@");
        driver.findElement(By.cssSelector(".w-100")).click();

        greenMail.setUser(FROM_EMAIL, Keys.get("EMAIL.USER"), Keys.get("EMAIL.PASSWORD"));

        driver.findElement(By.linkText("Resend mail")).click();

        MimeMessage[] messages = greenMail.getReceivedMessagesForDomain(EMAIL);
        Message message = messages[0];

        MimeMultipart content = (MimeMultipart) message.getContent();
        String emailMessage = content.getBodyPart(0).getContent().toString();

        int s = emailMessage.indexOf("(if");
        String actualText = emailMessage.substring(s);
        String expectedText =  "(if you did not register then just ignore this message)";

        assertEquals(expectedText, actualText);
    }

    @Test
    void resendMailUKTest() throws MessagingException, IOException {
        driver.get("http://127.0.0.1:7000/login");
        driver.findElement(By.linkText("\uD83C\uDDEC\uD83C\uDDE7")).click();
        driver.findElement(By.linkText("🇺🇦")).click();
        driver.findElement(By.linkText("Зареєструватися")).click();
        driver.findElement(By.id("email")).click();
        driver.findElement(By.id("email")).sendKeys("qwer@gmail.com");
        driver.findElement(By.id("password")).click();
        driver.findElement(By.id("password")).sendKeys("Qwerty123@");
        driver.findElement(By.id("conformation")).sendKeys("Qwerty123@");
        driver.findElement(By.cssSelector(".w-100")).click();

        greenMail.setUser(FROM_EMAIL, Keys.get("EMAIL.USER"), Keys.get("EMAIL.PASSWORD"));

        driver.findElement(By.linkText("Надіслати ще раз")).click();

        MimeMessage[] messages = greenMail.getReceivedMessagesForDomain(EMAIL);
        Message message = messages[0];

        MimeMultipart content = (MimeMultipart) message.getContent();
        String emailMessage = content.getBodyPart(0).getContent().toString();

        int s = emailMessage.indexOf("(Якщо");
        String actualText = emailMessage.substring(s);
        String expectedText =  "(Якщо ви не реєструвалися проігноруйте це повідомлення)";

        assertEquals(expectedText, actualText);
    }

    @Test
    void resetPasswordENTest() throws MessagingException, IOException {
        driver.get("http://127.0.0.1:7000/login");
        driver.findElement(By.linkText("Sign Up")).click();
        driver.findElement(By.id("email")).click();
        driver.findElement(By.id("email")).sendKeys("qwer@gmail.com");
        driver.findElement(By.id("password")).click();
        driver.findElement(By.id("password")).sendKeys("Qwerty123@");
        driver.findElement(By.id("conformation")).sendKeys("Qwerty123@");
        driver.findElement(By.cssSelector(".w-100")).click();
        driver.findElement(By.linkText("Logout")).click();

        greenMail.setUser(FROM_EMAIL, Keys.get("EMAIL.USER"), Keys.get("EMAIL.PASSWORD"));

        driver.findElement(By.linkText("Forgot password?")).sendKeys(org.openqa.selenium.Keys.ENTER);
        driver.findElement(By.id("web-email")).click();
        driver.findElement(By.id("web-email")).sendKeys("qwer@gmail.com");
        driver.findElement(By.cssSelector("div:nth-child(5) > .btn")).click();
        MimeMessage[] messages = greenMail.getReceivedMessagesForDomain(EMAIL);
        Message message = messages[0];

        MimeMultipart content = (MimeMultipart) message.getContent();
        String emailMessage = content.getBodyPart(0).getContent().toString();

        int s = emailMessage.indexOf("(if");
        String actualText = emailMessage.substring(s);
        String expectedText =  "(if you did not reset you password then just ignore this message)";

        assertEquals(expectedText, actualText);
    }

    @Test
    void resetPasswordUKTest() throws MessagingException, IOException {
        driver.get("http://127.0.0.1:7000/login");
        driver.findElement(By.linkText("Sign Up")).click();
        driver.findElement(By.id("email")).click();
        driver.findElement(By.id("email")).sendKeys("qwer@gmail.com");
        driver.findElement(By.id("password")).click();
        driver.findElement(By.id("password")).sendKeys("Qwerty123@");
        driver.findElement(By.id("conformation")).sendKeys("Qwerty123@");
        driver.findElement(By.cssSelector(".w-100")).click();
        driver.findElement(By.linkText("Logout")).click();

        greenMail.setUser(FROM_EMAIL, Keys.get("EMAIL.USER"), Keys.get("EMAIL.PASSWORD"));

        driver.findElement(By.linkText("\uD83C\uDDEC\uD83C\uDDE7")).click();
        driver.findElement(By.linkText("🇺🇦")).click();
        driver.findElement(By.linkText("Забули пароль?")).sendKeys(org.openqa.selenium.Keys.ENTER);
        driver.findElement(By.id("web-email")).click();
        driver.findElement(By.id("web-email")).sendKeys("qwer@gmail.com");
        driver.findElement(By.cssSelector("div:nth-child(5) > .btn")).click();
        MimeMessage[] messages = greenMail.getReceivedMessagesForDomain(EMAIL);
        Message message = messages[0];

        MimeMultipart content = (MimeMultipart) message.getContent();
        String emailMessage = content.getBodyPart(0).getContent().toString();

        int s = emailMessage.indexOf("(if");
        String actualText = emailMessage.substring(s);
        String expectedText = "(якщо ви не скидали свій пароль, просто проігноруйте це повідомлення)";

        assertEquals(expectedText, actualText);
    }
}
