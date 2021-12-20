package edu.sumdu.tss.elephant.E2E;

import edu.sumdu.tss.elephant.helper.Keys;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
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


class LoginTest {
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
        Keys.loadParams(new File("config.conf"));
        sql2o = new Sql2o("jdbc:postgresql://" + Keys.get("DB.URL") + ":" + Keys.get("DB.PORT") + "/" + Keys.get("DB.NAME"), Keys.get("DB.USERNAME"), Keys.get("DB.PASSWORD"));
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
    public void correctLogin() {
        int randNum = (int) Math.abs(Math.random() * (1 - 100) + 1);
        String mail = String.format("scaliariy1+%d@gmail.com", randNum);
        String password = "D5h6Cm8pmbDpyR2@";
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
        driver.findElement(By.linkText("Logout")).click();
        driver.get("http://localhost:7000/login");
        driver.manage().window().setSize(new Dimension(974, 1040));
        driver.findElement(By.id("floatingInput")).click();
        driver.findElement(By.id("floatingInput")).sendKeys(mail);
        driver.findElement(By.id("floatingPassword")).click();
        driver.findElement(By.id("floatingPassword")).sendKeys(password);
        driver.findElement(By.cssSelector(".checkbox > label")).click();
        driver.findElement(By.cssSelector(".w-100")).click();
        String actualURL = driver.getCurrentUrl();
        driver.findElement(By.linkText("Logout")).click();
        String expectedURL = "http://localhost:7000/home";
        assertEquals(expectedURL, actualURL);
    }

    @Test
    public void incorrectLogin() {
        int randNum1 = (int) Math.abs(Math.random() * (1 - 1000) + 1);
        int randNum2 = (int) Math.abs(Math.random() * (1 - 1000) + 1);
        String mail1 = String.format("scaliariy1+%d@gmail.com", randNum1);
        String mail2 = String.format("scaliariy1+%d@gmail.com", randNum2);
        String password1 = "D5h6Cm8pmbDpyR2@";
        String password2 = "D5h6Cm8pmbDpyR3@";
        driver.get("http://localhost:7000/registration");
        driver.manage().window().setSize(new Dimension(974, 1040));
        driver.findElement(By.linkText("Sign Up")).click();
        driver.findElement(By.id("email")).click();
        driver.findElement(By.id("email")).sendKeys(mail1);
        driver.findElement(By.id("password")).click();
        driver.findElement(By.id("password")).sendKeys(password1);
        driver.findElement(By.id("conformation")).click();
        driver.findElement(By.id("conformation")).sendKeys(password1);
        driver.findElement(By.cssSelector(".w-100")).click();
        driver.findElement(By.linkText("Logout")).click();
        driver.get("http://localhost:7000/login");
        driver.manage().window().setSize(new Dimension(974, 1040));
        driver.findElement(By.id("floatingInput")).click();
        driver.findElement(By.id("floatingInput")).sendKeys(mail2);
        driver.findElement(By.id("floatingPassword")).click();
        driver.findElement(By.id("floatingPassword")).sendKeys(password2);
        driver.findElement(By.cssSelector(".checkbox > label")).click();
        driver.findElement(By.cssSelector(".w-100")).click();
        String actualURL = driver.getCurrentUrl();
        String expectedURL = "http://localhost:7000/login";
        assertEquals(expectedURL, actualURL);
    }

    @Test
    public void logoutTest(){
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++)
            sb.append(AB.charAt(rnd.nextInt(AB.length())));
        String mail = String.format("scaliariy1+%s@gmail.com", sb);
        String password = "D5h6Cm8pmbDpyR2@";
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
        driver.findElement(By.linkText("Logout")).click();
        driver.get("http://localhost:7000/login");
        driver.manage().window().setSize(new Dimension(974, 1040));
        driver.findElement(By.id("floatingInput")).click();
        driver.findElement(By.id("floatingInput")).sendKeys(mail);
        driver.findElement(By.id("floatingPassword")).click();
        driver.findElement(By.id("floatingPassword")).sendKeys(password);
        driver.findElement(By.cssSelector(".checkbox > label")).click();
        driver.findElement(By.cssSelector(".w-100")).click();
        driver.findElement(By.linkText("Logout")).click();
        String actualURL = driver.getCurrentUrl();
        String expectedURL = "http://localhost:7000/login";
        assertEquals(expectedURL, actualURL);
    }
}


