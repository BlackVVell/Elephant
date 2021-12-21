package edu.sumdu.tss.elephant.E2E;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

import java.io.File;

import edu.sumdu.tss.elephant.helper.Keys;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;


class AccessToBackupsTest {
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
    public void creatingBackupsNotAuthedUser() {
        driver.manage().window().setSize(new Dimension(974, 1040));
        driver.get("http://localhost:7000/home");
        String actualURL = driver.getCurrentUrl();
        String expectedURL = "http://localhost:7000/home";
        assertNotEquals(expectedURL, actualURL);
    }

    @Test
    public void creatingBackupsNotCheckedUser() {
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

        String actual = driver.findElement(By.cssSelector(".container:nth-child(3)")).getText();
        int index = actual.lastIndexOf("You need to approve you email before can create new DB");
        assertNotEquals(index, -1);

        driver.findElement(By.linkText("Logout")).click();
    }

    @Test
    public void creatingBackupsBasicUser() {
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
        driver.findElement(By.cssSelector(".col:nth-child(1) .w-100")).sendKeys(org.openqa.selenium.Keys.RETURN);
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
    public void creatingBackupsProUser() {
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
        driver.findElement(By.cssSelector(".col:nth-child(2) .w-100")).sendKeys(org.openqa.selenium.Keys.RETURN);
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
    public void creatingBackupsAdminUser() {
    }

    @Test
    public void updateBackupsNotAuthedUser() {
        driver.manage().window().setSize(new Dimension(974, 1040));
        driver.get("http://localhost:7000/home");
        String actualURL = driver.getCurrentUrl();
        String expectedURL = "http://localhost:7000/home";
        assertNotEquals(expectedURL, actualURL);
    }

    @Test
    public void updateBackupsNotCheckedUser() {
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

        String actual = driver.findElement(By.cssSelector(".container:nth-child(3)")).getText();
        int index = actual.lastIndexOf("You need to approve you email before can create new DB");
        assertNotEquals(index, -1);

        driver.findElement(By.linkText("Logout")).click();
    }

    @Test
    public void updateBackupsBasicUser() {
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
        driver.findElement(By.cssSelector(".col:nth-child(1) .w-100")).sendKeys(org.openqa.selenium.Keys.RETURN);
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
    public void updateBackupsProUser() {
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
        driver.findElement(By.cssSelector(".col:nth-child(2) .w-100")).sendKeys(org.openqa.selenium.Keys.RETURN);
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
    public void updateBackupsAdminUser() {
    }

    @Test
    public void deleteBackupsNotAuthedUser() {
        driver.manage().window().setSize(new Dimension(974, 1040));
        driver.get("http://localhost:7000/home");
        String actualURL = driver.getCurrentUrl();
        String expectedURL = "http://localhost:7000/home";
        assertNotEquals(expectedURL, actualURL);
    }

    @Test
    public void deleteBackupsNotCheckedUser() {
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

        String actual = driver.findElement(By.cssSelector(".container:nth-child(3)")).getText();
        int index = actual.lastIndexOf("You need to approve you email before can create new DB");
        assertNotEquals(index, -1);

        driver.findElement(By.linkText("Logout")).click();
    }

    @Test
    public void deleteBackupsBasicUser() {
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
        driver.findElement(By.cssSelector(".col:nth-child(1) .w-100")).sendKeys(org.openqa.selenium.Keys.RETURN);
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
    public void deleteBackupsProUser() {
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
        driver.findElement(By.cssSelector(".col:nth-child(2) .w-100")).sendKeys(org.openqa.selenium.Keys.RETURN);
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
    public void deleteBackupsAdminUser() {
    }


}