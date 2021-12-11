package edu.sumdu.tss.elephant.helper.utils;

import edu.sumdu.tss.elephant.helper.exception.BackupException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

class CmdUtilTest {

    private final PrintStream standardOut = System.out;
    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();


    @BeforeEach
    public void setUp() {
        System.setOut(new PrintStream(outputStreamCaptor));
    }

    @AfterEach
    public void tearDown() {
        System.setOut(standardOut);
    }

    @Test
    void execTest() {

        String command = "clear";
        CmdUtil.exec(command);

        assertEquals("Perform: clear", outputStreamCaptor.toString()
                .trim());
    }

    @Test
    void execBackExTest(){
        String command = "sudo";
        assertThrows(BackupException.class, () -> {
            CmdUtil.exec(command);
        });
    }
    @Test
    void execBackIoExTest(){
        String command = ".";
        assertThrows(BackupException.class, () -> {
            CmdUtil.exec(command);
        });
    }

    @Test
    void execBackInterExTest(){
        String command = ";";
        assertThrows(BackupException.class, () -> {
            CmdUtil.exec(command);
        });
    }

}