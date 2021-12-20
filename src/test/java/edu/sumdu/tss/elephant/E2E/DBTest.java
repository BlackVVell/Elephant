package edu.sumdu.tss.elephant.E2E;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;


class DBTest {
    private WebDriver driver;
    private Map<String, Object> vars;
    JavascriptExecutor js;
    final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    SecureRandom rnd = new SecureRandom();
    final int len = 10;
    private static Sql2o sql2o;

    @BeforeEach
    public void setUp() {
        driver = new ChromeDriver();
        js = (JavascriptExecutor) driver;
        vars = new HashMap<String, Object>();
        edu.sumdu.tss.elephant.helper.Keys.loadParams(new File("config.conf"));
        sql2o = new Sql2o("jdbc:postgresql://" + edu.sumdu.tss.elephant.helper.Keys.get("DB.URL") + ":" + edu.sumdu.tss.elephant.helper.Keys.get("DB.PORT") + "/" + edu.sumdu.tss.elephant.helper.Keys.get("DB.NAME"), edu.sumdu.tss.elephant.helper.Keys.get("DB.USERNAME"), edu.sumdu.tss.elephant.helper.Keys.get("DB.PASSWORD"));
    }

    @AfterEach
    public void tearDown() {
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
    public void correctDBCreate() {
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++)
            sb.append(AB.charAt(rnd.nextInt(AB.length())));
        String mail = String.format("scaliariy1+%s@gmail.com", sb);
        String password = "D5h6Cm8pmbDpyR2@";
        System.out.println(mail);
        System.out.println(password);
        driver.get("http://localhost:7000/registration");
        driver.manage().window().setSize(new Dimension(974, 1040));
        driver.findElement(By.linkText("Sign Up")).click();
        driver.findElement(By.id("email")).click();
        driver.findElement(By.id("email")).sendKeys(mail);
        driver.findElement(By.id("password")).click();
        driver.findElement(By.id("password")).sendKeys(password);
        driver.findElement(By.id("conformation")).click();
        driver.findElement(By.id("conformation")).sendKeys(password);
        driver.findElement(By.cssSelector(".w-100")).click();

        driver.findElement(By.linkText("Profile")).click();
        driver.findElement(By.cssSelector(".col:nth-child(1) .w-100")).sendKeys(Keys.RETURN);
        driver.findElement(By.linkText("Dashboard")).click();
        driver.findElement(By.cssSelector(".w-100")).click();
        driver.get("http://localhost:7000/home");

        String actualNumber = driver.findElement(By.cssSelector(".card:nth-child(2) span:nth-child(1)")).getText();
        driver.findElement(By.linkText("Logout")).click();
        String expectedNumber = "1";
        assertEquals(expectedNumber, actualNumber);
    }

    @Test
    public void incorrectDBCreate() {
    }

    @Test
    public void correctConnectDB() {
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++)
            sb.append(AB.charAt(rnd.nextInt(AB.length())));
        String mail = String.format("scaliariy1+%s@gmail.com", sb);
        System.out.println(mail);
        String password = "D5h6Cm8pmbDpyR2@";
        System.out.println(password);
        driver.get("http://localhost:7000/registration");
        driver.manage().window().setSize(new Dimension(974, 1040));
        driver.findElement(By.linkText("Sign Up")).click();
        driver.findElement(By.id("email")).click();
        driver.findElement(By.id("email")).sendKeys(mail);
        driver.findElement(By.id("password")).click();
        driver.findElement(By.id("password")).sendKeys(password);
        driver.findElement(By.id("conformation")).click();
        driver.findElement(By.id("conformation")).sendKeys(password);
        driver.findElement(By.cssSelector(".w-100")).click();

        driver.findElement(By.linkText("Profile")).click();
        driver.findElement(By.cssSelector(".col:nth-child(1) .w-100")).sendKeys(Keys.RETURN);
        driver.findElement(By.linkText("Dashboard")).click();
        driver.findElement(By.cssSelector(".w-100")).click();
        driver.get("http://localhost:7000/home");

        String actualDB = driver.findElement(By.cssSelector(".card-body:nth-child(1) h6")).getText();
        driver.findElement(By.cssSelector(".card-body:nth-child(1) h6")).click();
        String actualURL = driver.getCurrentUrl();
        String expectedURL = String.format("http://localhost:7000/database/%s", actualDB);
        assertEquals(expectedURL, actualURL);
        driver.findElement(By.linkText("Logout")).click();
    }

    @Test
    public void correctDeleteDB() {
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++)
            sb.append(AB.charAt(rnd.nextInt(AB.length())));
        String mail = String.format("scaliariy1+%s@gmail.com", sb);
        System.out.println(mail);
        String password = "D5h6Cm8pmbDpyR2@";
        System.out.println(password);
        driver.get("http://localhost:7000/registration");
        driver.manage().window().setSize(new Dimension(974, 1040));
        driver.findElement(By.linkText("Sign Up")).click();
        driver.findElement(By.id("email")).click();
        driver.findElement(By.id("email")).sendKeys(mail);
        driver.findElement(By.id("password")).click();
        driver.findElement(By.id("password")).sendKeys(password);
        driver.findElement(By.id("conformation")).click();
        driver.findElement(By.id("conformation")).sendKeys(password);
        driver.findElement(By.cssSelector(".w-100")).click();
        driver.findElement(By.linkText("Profile")).click();
        driver.findElement(By.cssSelector(".col:nth-child(1) .w-100")).sendKeys(Keys.RETURN);
        driver.findElement(By.linkText("Dashboard")).click();
        driver.findElement(By.cssSelector(".w-100")).click();
        driver.get("http://localhost:7000/home");
        driver.findElement(By.cssSelector(".btn > .md")).click();
        driver.get("http://localhost:7000/home");

        String actualNumber = driver.findElement(By.cssSelector(".card:nth-child(2) span:nth-child(1)")).getText();
        String expectedNumber = "0";
        assertEquals(expectedNumber, actualNumber);
        driver.findElement(By.linkText("Logout")).click();
    }

    @Test
    public void incorrectDeleteDB() {
    }

    @Test
    public void changeDBPassword(){
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++)
            sb.append(AB.charAt(rnd.nextInt(AB.length())));
        String mail = String.format("scaliariy1+%s@gmail.com", sb);
        System.out.println(mail);
        String password = "D5h6Cm8pmbDpyR2@";
        System.out.println(password);
        driver.get("http://localhost:7000/registration");
        driver.manage().window().setSize(new Dimension(974, 1040));
        driver.findElement(By.linkText("Sign Up")).click();
        driver.findElement(By.id("email")).click();
        driver.findElement(By.id("email")).sendKeys(mail);
        driver.findElement(By.id("password")).click();
        driver.findElement(By.id("password")).sendKeys(password);
        driver.findElement(By.id("conformation")).click();
        driver.findElement(By.id("conformation")).sendKeys(password);
        driver.findElement(By.cssSelector(".w-100")).click();
        driver.findElement(By.linkText("Profile")).click();
        driver.findElement(By.id("db-password")).clear();
        driver.findElement(By.id("db-password")).sendKeys("aaaaaaaaaa");
        String actualPSW = driver.findElement(By.id("db-password")).getAttribute("value");
        driver.findElement(By.cssSelector(".container:nth-child(4) .btn")).click();
        assertEquals(actualPSW, "aaaaaaaaaa");
    }

    @Test
    public void viewDBTables() {
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++)
            sb.append(AB.charAt(rnd.nextInt(AB.length())));
        String mail = String.format("scaliariy1+%s@gmail.com", sb);
        System.out.println(mail);
        String password = "D5h6Cm8pmbDpyR2@";
        System.out.println(password);
        driver.get("http://localhost:7000/registration");
        driver.manage().window().setSize(new Dimension(974, 1040));
        driver.findElement(By.linkText("Sign Up")).click();
        driver.findElement(By.id("email")).click();
        driver.findElement(By.id("email")).sendKeys(mail);
        driver.findElement(By.id("password")).click();
        driver.findElement(By.id("password")).sendKeys(password);
        driver.findElement(By.id("conformation")).click();
        driver.findElement(By.id("conformation")).sendKeys(password);
        driver.findElement(By.cssSelector(".w-100")).click();

        driver.findElement(By.linkText("Profile")).click();
        driver.findElement(By.cssSelector(".col:nth-child(1) .w-100")).sendKeys(Keys.RETURN);
        driver.findElement(By.linkText("Dashboard")).click();
        driver.findElement(By.cssSelector(".w-100")).click();
        driver.get("http://localhost:7000/home");

        String actualDB = driver.findElement(By.cssSelector(".card-body:nth-child(1) h6")).getText();
        driver.findElement(By.cssSelector(".card-body:nth-child(1) h6")).click();
        driver.findElement(By.cssSelector(".col:nth-child(1) .card-title")).click();

        String actualURL = driver.getCurrentUrl();
        String expectedURL = String.format("http://localhost:7000/database/%s/table", actualDB);
        assertEquals(expectedURL, actualURL);
        driver.findElement(By.linkText("Logout")).click();
    }
}

