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
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;


class RegistrationTest {
    private WebDriver driver;
    private Map<String, Object> vars;
    JavascriptExecutor js;
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
    public void correctRegistration() {
        int randNum = (int) Math.abs(Math.random() * (1 - 1000) + 1);
        String mail = String.format("scaliariy1+%d@gmail.com",randNum);
        driver.get("http://localhost:7000/registration");
        driver.manage().window().setSize(new Dimension(974, 1040));
        driver.findElement(By.linkText("Sign Up")).click();
        driver.findElement(By.id("email")).click();
        driver.findElement(By.id("email")).sendKeys(mail);
        driver.findElement(By.id("password")).click();
        driver.findElement(By.id("password")).sendKeys("D5h6Cm8pmbDpyR2@");
        driver.findElement(By.id("conformation")).click();
        driver.findElement(By.id("conformation")).sendKeys("D5h6Cm8pmbDpyR2@");
        driver.findElement(By.cssSelector(".w-100")).click();
        String actualURL = driver.getCurrentUrl();
        driver.findElement(By.linkText("Logout")).click();
        String expectedURL = "http://localhost:7000/home";
        assertEquals(expectedURL, actualURL);
    }

    @Test
    public void incorrectRegPasswordAndLogin() {
        int randNum = (int) Math.abs(Math.random() * (1 - 1000) + 1);
        String mail = String.format("scaliariy1+testsdadadadsadadadasdasdaddaddfsfsfsfsfsfsfsfsdff%d@gmail.com",randNum);
        String password = "000000000000000";
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
        String actualURL = driver.getCurrentUrl();
        String expectedURL = "http://localhost:7000/registration";
        assertEquals(expectedURL, actualURL);
    }

    @Test
    public void incorrectRegMailExist() {
        int randNum = (int) Math.abs(Math.random() * (1 - 1000) + 1);
        String mail = String.format("scaliariy1+%d@gmail.com",randNum);
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
        driver.get("http://localhost:7000/registration");
        driver.findElement(By.linkText("Sign Up")).click();
        driver.findElement(By.id("email")).click();
        driver.findElement(By.id("email")).sendKeys(mail);
        driver.findElement(By.id("password")).click();
        driver.findElement(By.id("password")).sendKeys(password);
        driver.findElement(By.id("conformation")).click();
        driver.findElement(By.id("conformation")).sendKeys(password);
        driver.findElement(By.cssSelector(".w-100")).click();
        String actualURL = driver.getCurrentUrl();
        String expectedURL = "http://localhost:7000/registration";
        assertEquals(expectedURL, actualURL);
    }

    @Test
    public void incorrectRegPassword() {
        int randNum = (int) Math.abs(Math.random() * (1 - 1000) + 1);
        String mail = String.format("scaliariy1+test%d@gmail.com",randNum);
        String password = "000000000000000";
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
        String actualURL = driver.getCurrentUrl();
        String expectedURL = "http://localhost:7000/registration";
        assertEquals(expectedURL, actualURL);
    }

    @Test
    public void incorrectRegLogin() {
        int randNum = (int) Math.abs(Math.random() * (1 - 1000) + 1);
        String mail = String.format("scaliariy1+testsdadadadsadadadasdasdaddaddfsfsfsfsfsfsfsfsdff%d",randNum);
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
        String actualURL = driver.getCurrentUrl();
        String expectedURL = "http://localhost:7000/registration";
        assertEquals(expectedURL, actualURL);
    }

    @Test
    public void incorrectRegRepeatPassword() {
        int randNum = (int) Math.abs(Math.random() * (1 - 1000) + 1);
        String mail = String.format("scaliariy1+testsdadadadsadadadasdasdaddaddfsfsfsfsfsfsfsfsdff%d",randNum);
        String password1 = "D5h6Cm8pmbDpyR2@";
        String password2 = "D5h6Cm8pmbDpyR3@";
        driver.get("http://localhost:7000/registration");
        driver.manage().window().setSize(new Dimension(974, 1040));
        driver.findElement(By.linkText("Sign Up")).click();
        driver.findElement(By.id("email")).click();
        driver.findElement(By.id("email")).sendKeys(mail);
        driver.findElement(By.id("password")).click();
        driver.findElement(By.id("password")).sendKeys(password1);
        driver.findElement(By.id("conformation")).click();
        driver.findElement(By.id("conformation")).sendKeys(password2);
        driver.findElement(By.cssSelector(".w-100")).click();
        String actualURL = driver.getCurrentUrl();
        String expectedURL = "http://localhost:7000/registration";
        assertEquals(expectedURL, actualURL);
    }
}

