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

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LanguageSiteTest {
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
    void siteENNonAuthedUserTest() {
        driver.get("http://127.0.0.1:7000/login");
        String signUp = driver.findElement(By.linkText("Sign Up")).getText();
        driver.findElement(By.linkText("Sign Up")).click();
        assertEquals("http://127.0.0.1:7000/registration", driver.getCurrentUrl());
        String signIn = driver.findElement(By.linkText("Sign In")).getText();
        assertEquals("Sign Up", signUp);
        assertEquals("Sign In", signIn);
    }

    @Test
    void siteUANonAuthedUserTest() {
        driver.get("http://127.0.0.1:7000/login");
        driver.findElement(By.linkText("\uD83C\uDDEC\uD83C\uDDE7")).click();
        driver.findElement(By.linkText("🇺🇦")).click();
        String signUp = driver.findElement(By.linkText("Зареєструватися")).getText();
        driver.findElement(By.linkText("Зареєструватися")).click();
        assertEquals("http://127.0.0.1:7000/registration", driver.getCurrentUrl());
        String signIn = driver.findElement(By.linkText("Увійти")).getText();
        assertEquals("Зареєструватися", signUp);
        assertEquals("Увійти", signIn);
    }

    @Test
    void siteENAuthedUserTest() {
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


    }

    @Test
    void siteUKAuthedUserTest() {
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
        driver.findElement(By.linkText("Ukraine")).click();
        assertEquals("It's your", role);


    }
}
