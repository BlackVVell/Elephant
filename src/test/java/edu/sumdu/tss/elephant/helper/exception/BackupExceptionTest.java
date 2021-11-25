package edu.sumdu.tss.elephant.helper.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BackupExceptionTest {

    @Test
    void BackupExceptionWithEx(){
        NullPointerException exception = new NullPointerException();
        BackupException backupException = new BackupException(exception);
        assertEquals(exception,backupException.getCause());
    }

    @Test
    void BackupExceptionWithMess(){
        String equals = "edu.sumdu.tss.elephant.helper.exception.BackupException: Error";
        String massage = "Error";
        BackupException backupException = new BackupException(massage);
        assertEquals(equals,backupException.toString());
    }

    @Test
    void BackupExceptionWithExAndMess(){
        NullPointerException exception = new NullPointerException();
        String equals = "edu.sumdu.tss.elephant.helper.exception.BackupException: Error";
        String message = "Error";
        BackupException backupException = new BackupException(message,exception);
        assertEquals(equals,backupException.toString());
    }

}