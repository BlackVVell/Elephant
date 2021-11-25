package edu.sumdu.tss.elephant.helper.utils;

import edu.sumdu.tss.elephant.helper.exception.BackupException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CmdUtilTest {

    @Test
    void execTest() {
        String command = "ls";
        CmdUtil.exec(command);
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