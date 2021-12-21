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


class BackupsTest {
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
    public void correctCreateBackups() {
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

        driver.findElement(By.cssSelector("h6")).click();
        driver.findElement(By.cssSelector(".col:nth-child(2) .card-title")).click();
        driver.findElement(By.id("floatingInput")).clear();
        driver.findElement(By.id("floatingInput")).sendKeys("1");
        driver.findElement(By.cssSelector(".w-100")).click();

        String actualBackUp = driver.findElement(By.cssSelector(".text-small")).getText();
        assertEquals(actualBackUp, "1");

        driver.findElement(By.linkText("Logout")).click();
    }

    @Test
    public void incorrectCreateBackupsLimitExceeded() {
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

        driver.findElement(By.cssSelector("h6")).click();
        driver.findElement(By.cssSelector(".col:nth-child(2) .card-title")).click();

        driver.findElement(By.id("floatingInput")).clear();
        driver.findElement(By.id("floatingInput")).sendKeys("1");
        driver.findElement(By.cssSelector(".w-100")).click();

        driver.findElement(By.id("floatingInput")).clear();
        driver.findElement(By.id("floatingInput")).sendKeys("1");
        driver.findElement(By.cssSelector(".w-100")).click();

        String alert = driver.findElement(By.cssSelector(".alert")).getText();
        assertEquals(alert, "You limit reached");

        driver.findElement(By.linkText("Logout")).click();
    }

    @Test
    public void correctUpdateBackups() {
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
        driver.findElement(By.cssSelector(".col:nth-child(2) .w-100")).sendKeys(Keys.RETURN);
        driver.findElement(By.linkText("Dashboard")).click();
        driver.findElement(By.cssSelector(".w-100")).click();
        driver.get("http://localhost:7000/home");

        driver.findElement(By.cssSelector("h6")).click();
        driver.findElement(By.cssSelector(".col:nth-child(2) .card-title")).click();
        driver.findElement(By.id("floatingInput")).clear();
        driver.findElement(By.id("floatingInput")).sendKeys("1");
        driver.findElement(By.cssSelector(".w-100")).click();

        driver.findElement(By.cssSelector(".d-inline:nth-child(1) > input")).click();

        String alert = driver.findElement(By.cssSelector(".alert")).getText();
        assertEquals(alert, "Backup created successfully");

        driver.findElement(By.linkText("Logout")).click();
    }

    @Test
    public void incorrectUpdateBackupsBasicUserCannotUpdateBackup() {
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

        driver.findElement(By.cssSelector("h6")).click();
        driver.findElement(By.cssSelector(".col:nth-child(2) .card-title")).click();
        driver.findElement(By.id("floatingInput")).clear();
        driver.findElement(By.id("floatingInput")).sendKeys("1");
        driver.findElement(By.cssSelector(".w-100")).click();

        driver.findElement(By.cssSelector(".d-inline:nth-child(1) > input")).click();

        String alert = driver.findElement(By.cssSelector(".alert")).getText();
        assertEquals(alert, "You limit reached");

        driver.findElement(By.linkText("Logout")).click();
    }

    @Test
    public void correctResetDBToBackup() {
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

        driver.findElement(By.cssSelector("h6")).click();
        driver.findElement(By.cssSelector(".col:nth-child(2) .card-title")).click();
        driver.findElement(By.id("floatingInput")).clear();
        driver.findElement(By.id("floatingInput")).sendKeys("1");
        driver.findElement(By.cssSelector(".w-100")).click();

        driver.findElement(By.cssSelector(".d-inline:nth-child(2) > input")).click();

        String alert = driver.findElement(By.cssSelector(".alert")).getText();
        assertEquals(alert, "Restore performed successfully");

        driver.findElement(By.linkText("Logout")).click();
    }

    @Test
    public void incorrectResetDBToBackup() {
    }

    @Test
    public void correctDeleteBackups() {
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

        driver.findElement(By.cssSelector("h6")).click();
        driver.findElement(By.cssSelector(".col:nth-child(2) .card-title")).click();
        driver.findElement(By.id("floatingInput")).clear();
        driver.findElement(By.id("floatingInput")).sendKeys("1");
        driver.findElement(By.cssSelector(".w-100")).click();

        driver.findElement(By.cssSelector(".d-inline:nth-child(3) > input")).click();

        String textField = driver.findElement(By.cssSelector("p")).getText();
        assertEquals(textField, "Currently you have no one backups on this database");

        driver.findElement(By.linkText("Logout")).click();
    }

    @Test
    public void incorrectDeleteBackups() {
    }

}
