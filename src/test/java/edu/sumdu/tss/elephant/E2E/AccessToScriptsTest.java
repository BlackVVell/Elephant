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


class AccessToScriptsTest {
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
    public void uploadScriptsNotAuthedUser() {
        driver.manage().window().setSize(new Dimension(974, 1040));
        driver.get("http://localhost:7000/home");
        String actualURL = driver.getCurrentUrl();
        String expectedURL = "http://localhost:7000/home";
        assertNotEquals(expectedURL, actualURL);
    }

    @Test
    public void uploadScriptsNotCheckedUser() {
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
    public void uploadScriptsBasicUser() {
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
        driver.findElement(By.cssSelector(".col:nth-child(1) .w-100")).sendKeys(org.openqa.selenium.Keys.RETURN);
        driver.findElement(By.linkText("Dashboard")).click();
        driver.findElement(By.cssSelector(".w-100")).click();
        driver.get("http://localhost:7000/home");

        driver.findElement(By.cssSelector("h6")).click();
        driver.findElement(By.cssSelector(".col:nth-child(3) .card-title")).click();

        File file = new File("script.sql");
        driver.findElement(By.name("file")).sendKeys(file.getAbsolutePath());
        driver.findElement(By.cssSelector(".w-100")).click();

        String script = driver.findElement(By.cssSelector("h6")).getText();
        assertEquals(script, "script.sql");
        driver.findElement(By.linkText("Logout")).click();
    }

    @Test
    public void uploadScriptsProUser() {
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
        driver.findElement(By.cssSelector(".col:nth-child(2) .w-100")).sendKeys(org.openqa.selenium.Keys.RETURN);
        driver.findElement(By.linkText("Dashboard")).click();
        driver.findElement(By.cssSelector(".w-100")).click();
        driver.get("http://localhost:7000/home");

        driver.findElement(By.cssSelector("h6")).click();
        driver.findElement(By.cssSelector(".col:nth-child(3) .card-title")).click();

        File file = new File("script.sql");
        driver.findElement(By.name("file")).sendKeys(file.getAbsolutePath());
        driver.findElement(By.cssSelector(".w-100")).click();

        String script = driver.findElement(By.cssSelector("h6")).getText();
        assertEquals(script, "script.sql");
        driver.findElement(By.linkText("Logout")).click();
    }

    @Test
    public void uploadScriptsAdminUser() {
    }

    @Test
    public void launchScriptsNotAuthedUser() {
        driver.manage().window().setSize(new Dimension(974, 1040));
        driver.get("http://localhost:7000/home");
        String actualURL = driver.getCurrentUrl();
        String expectedURL = "http://localhost:7000/home";
        assertNotEquals(expectedURL, actualURL);
    }

    @Test
    public void launchScriptsNotCheckedUser() {
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
    public void launchScriptsBasicUser() {
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
        driver.findElement(By.cssSelector(".col:nth-child(1) .w-100")).sendKeys(org.openqa.selenium.Keys.RETURN);
        driver.findElement(By.linkText("Dashboard")).click();
        driver.findElement(By.cssSelector(".w-100")).click();
        driver.get("http://localhost:7000/home");

        driver.findElement(By.cssSelector("h6")).click();
        driver.findElement(By.cssSelector(".col:nth-child(3) .card-title")).click();

        File file = new File("script.sql");
        driver.findElement(By.name("file")).sendKeys(file.getAbsolutePath());
        driver.findElement(By.cssSelector(".w-100")).click();

        driver.findElement(By.cssSelector(".d-inline:nth-child(1) .md")).click();

        String text = driver.findElement(By.cssSelector("tr:nth-child(2) > td:nth-child(2)")).getText();
        assertEquals(text, "020000???????????? ???? ???????????? ??????????????????????.");
        driver.findElement(By.linkText("Logout")).click();
    }

    @Test
    public void launchScriptsProUser() {
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
        driver.findElement(By.cssSelector(".col:nth-child(2) .w-100")).sendKeys(org.openqa.selenium.Keys.RETURN);
        driver.findElement(By.linkText("Dashboard")).click();
        driver.findElement(By.cssSelector(".w-100")).click();
        driver.get("http://localhost:7000/home");

        driver.findElement(By.cssSelector("h6")).click();
        driver.findElement(By.cssSelector(".col:nth-child(3) .card-title")).click();

        File file = new File("script.sql");
        driver.findElement(By.name("file")).sendKeys(file.getAbsolutePath());
        driver.findElement(By.cssSelector(".w-100")).click();

        driver.findElement(By.cssSelector(".d-inline:nth-child(1) .md")).click();

        String text = driver.findElement(By.cssSelector("tr:nth-child(2) > td:nth-child(2)")).getText();
        assertEquals(text, "020000???????????? ???? ???????????? ??????????????????????.");
        driver.findElement(By.linkText("Logout")).click();
    }

    @Test
    public void launchScriptsAdminUser() {
    }

    @Test
    public void deletionScriptsNotAuthedUser() {
        driver.manage().window().setSize(new Dimension(974, 1040));
        driver.get("http://localhost:7000/home");
        String actualURL = driver.getCurrentUrl();
        String expectedURL = "http://localhost:7000/home";
        assertNotEquals(expectedURL, actualURL);
    }

    @Test
    public void deletionScriptsNotCheckedUser() {
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
    public void deletionScriptsBasicUser() {
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
        driver.findElement(By.cssSelector(".col:nth-child(1) .w-100")).sendKeys(org.openqa.selenium.Keys.RETURN);
        driver.findElement(By.linkText("Dashboard")).click();
        driver.findElement(By.cssSelector(".w-100")).click();
        driver.get("http://localhost:7000/home");

        driver.findElement(By.cssSelector("h6")).click();
        driver.findElement(By.cssSelector(".col:nth-child(3) .card-title")).click();

        File file = new File("script.sql");
        driver.findElement(By.name("file")).sendKeys(file.getAbsolutePath());
        driver.findElement(By.cssSelector(".w-100")).click();

        driver.findElement(By.cssSelector(".d-inline:nth-child(2) > .btn")).click();

        String text = driver.findElement(By.cssSelector("p")).getText();
        assertEquals(text, "Currently you have no one script");
        driver.findElement(By.linkText("Logout")).click();
    }

    @Test
    public void deletionScriptsProUser() {
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
        driver.findElement(By.cssSelector(".col:nth-child(2) .w-100")).sendKeys(org.openqa.selenium.Keys.RETURN);
        driver.findElement(By.linkText("Dashboard")).click();
        driver.findElement(By.cssSelector(".w-100")).click();
        driver.get("http://localhost:7000/home");

        driver.findElement(By.cssSelector("h6")).click();
        driver.findElement(By.cssSelector(".col:nth-child(3) .card-title")).click();

        File file = new File("script.sql");
        driver.findElement(By.name("file")).sendKeys(file.getAbsolutePath());
        driver.findElement(By.cssSelector(".w-100")).click();

        driver.findElement(By.cssSelector(".d-inline:nth-child(2) > .btn")).click();

        String text = driver.findElement(By.cssSelector("p")).getText();
        assertEquals(text, "Currently you have no one script");
        driver.findElement(By.linkText("Logout")).click();
    }

    @Test
    public void deletionScriptsAdminUser() {
    }

    @Test
    public void runSQLNotAuthedUser() {
        driver.manage().window().setSize(new Dimension(974, 1040));
        driver.get("http://localhost:7000/home");
        String actualURL = driver.getCurrentUrl();
        String expectedURL = "http://localhost:7000/home";
        assertNotEquals(expectedURL, actualURL);
    }

    @Test
    public void runSQLNotCheckedUser() {
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
    public void runSQLBasicUser() {
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
        driver.findElement(By.cssSelector(".col:nth-child(1) .w-100")).sendKeys(org.openqa.selenium.Keys.RETURN);
        driver.findElement(By.linkText("Dashboard")).click();
        driver.findElement(By.cssSelector(".w-100")).click();
        driver.get("http://localhost:7000/home");

        driver.findElement(By.cssSelector("h6")).click();
        driver.findElement(By.cssSelector(".col:nth-child(4) .card-body")).click();
        driver.findElement(By.cssSelector(".ace_text-input")).sendKeys("create table tab1 (id Integer);\n\n");
        driver.findElement(By.cssSelector(".btn:nth-child(1)")).click();

        String actual = driver.findElement(By.cssSelector(".query")).getText();

        String expected = "create table tab1 (id Integer)";
        assertEquals(expected, actual);
        driver.findElement(By.linkText("Logout")).click();
    }

    @Test
    public void runSQLProUser() {
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
        driver.findElement(By.cssSelector(".col:nth-child(2) .w-100")).sendKeys(org.openqa.selenium.Keys.RETURN);
        driver.findElement(By.linkText("Dashboard")).click();
        driver.findElement(By.cssSelector(".w-100")).click();
        driver.get("http://localhost:7000/home");

        driver.findElement(By.cssSelector("h6")).click();
        driver.findElement(By.cssSelector(".col:nth-child(4) .card-body")).click();
        driver.findElement(By.cssSelector(".ace_text-input")).sendKeys("create table tab1 (id Integer);\n\n");
        driver.findElement(By.cssSelector(".btn:nth-child(1)")).click();

        String actual = driver.findElement(By.cssSelector(".query")).getText();

        String expected = "create table tab1 (id Integer)";
        assertEquals(expected, actual);
        driver.findElement(By.linkText("Logout")).click();
    }

    @Test
    public void runSQLAdminUser() {
    }
}