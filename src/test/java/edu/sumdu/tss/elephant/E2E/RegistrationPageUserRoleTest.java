package edu.sumdu.tss.elephant.E2E;

import com.icegreen.greenmail.store.FolderException;
import edu.sumdu.tss.elephant.helper.Keys;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.io.File;

public class RegistrationPageUserRoleTest {
    private WebDriver driver;
    private static Sql2o sql2o;

    @BeforeAll
    static void setUp() {
        Keys.loadParams(new File("config.conf"));
        sql2o = new Sql2o("jdbc:postgresql://" + Keys.get("DB.URL") + ":" + Keys.get("DB.PORT") + "/" + Keys.get("DB.NAME"), Keys.get("DB.USERNAME"), Keys.get("DB.PASSWORD"));
    }

    @BeforeEach
    void clearDb() throws FolderException {
        driver = new ChromeDriver();
    }

    @AfterEach
    void driverQuit() {
        driver.quit();
        try (Connection connection = sql2o.open()) {
            connection.createQuery("DELETE FROM BACKUPS").executeUpdate();
            connection.createQuery("DELETE FROM DATABASES").executeUpdate();
            connection.createQuery("DELETE FROM LOGGER").executeUpdate();
            connection.createQuery("DELETE FROM SCRIPTS").executeUpdate();
            connection.createQuery("DELETE FROM USERS").executeUpdate();
        }
    }

    @Test
    void registrationPageNonAuthedUserTest() {
        driver.get("http://127.0.0.1:7000/login");
        driver.findElement(By.linkText("Sign Up")).click();
        String url = driver.getCurrentUrl();
        assertEquals("http://127.0.0.1:7000/registration", url);
    }

    @Test
    void registrationPageNotCheckedUserTest() {
        driver.get("http://127.0.0.1:7000/login");
        driver.findElement(By.linkText("Sign Up")).click();
        driver.findElement(By.id("email")).click();
        driver.findElement(By.id("email")).sendKeys("qwer@gmail.com");
        driver.findElement(By.id("password")).click();
        driver.findElement(By.id("password")).sendKeys("Qwerty123@");
        driver.findElement(By.id("conformation")).sendKeys("Qwerty123@");
        driver.findElement(By.cssSelector(".w-100")).click();
        driver.get("http://127.0.0.1:7000/registration");
        String url = driver.getCurrentUrl();
        assertNotEquals("http://127.0.0.1:7000/registration", url);
    }

    @Test
    void registrationPageBasicUserTest() {
        driver.get("http://127.0.0.1:7000/login");
        driver.findElement(By.linkText("Sign Up")).click();
        driver.findElement(By.id("email")).click();
        driver.findElement(By.id("email")).sendKeys("qwer@gmail.com");
        driver.findElement(By.id("password")).click();
        driver.findElement(By.id("password")).sendKeys("Qwerty123@");
        driver.findElement(By.id("conformation")).sendKeys("Qwerty123@");
        driver.findElement(By.cssSelector(".w-100")).click();
        driver.findElement(By.linkText("Profile")).click();
        driver.findElement(By.cssSelector(".col:nth-child(1) .w-100")).sendKeys(org.openqa.selenium.Keys.ENTER);
        driver.findElement(By.linkText("Profile")).click();
        String role = driver.findElement(By.cssSelector(".col:nth-child(1) .w-100")).getText();
        assertEquals("It's your", role);

        driver.get("http://127.0.0.1:7000/registration");
        String url = driver.getCurrentUrl();
        assertNotEquals("http://127.0.0.1:7000/registration", url);
    }

    @Test
    void registrationPageProUserTest() {
        driver.get("http://127.0.0.1:7000/login");
        driver.findElement(By.linkText("Sign Up")).click();
        driver.findElement(By.id("email")).click();
        driver.findElement(By.id("email")).sendKeys("qwer@gmail.com");
        driver.findElement(By.id("password")).click();
        driver.findElement(By.id("password")).sendKeys("Qwerty123@");
        driver.findElement(By.id("conformation")).sendKeys("Qwerty123@");
        driver.findElement(By.cssSelector(".w-100")).click();
        driver.findElement(By.linkText("Profile")).click();
        driver.findElement(By.cssSelector(".col:nth-child(2) .w-100")).sendKeys(org.openqa.selenium.Keys.ENTER);
        driver.findElement(By.linkText("Profile")).click();
        String role = driver.findElement(By.cssSelector(".col:nth-child(2) .w-100")).getText();
        assertEquals("It's your", role);

        driver.get("http://127.0.0.1:7000/registration");
        String url = driver.getCurrentUrl();
        assertNotEquals("http://127.0.0.1:7000/registration", url);
    }
}
