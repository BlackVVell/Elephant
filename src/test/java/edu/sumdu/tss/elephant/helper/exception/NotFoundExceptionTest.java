package edu.sumdu.tss.elephant.helper.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NotFoundExceptionTest {

    @Test
    void getCode() {
        String massage = "Error";
        int code = 404;
        NotFoundException notFoundException = new NotFoundException(massage);

        assertEquals(notFoundException.getCode(),code);
    }
    @Test
    void NotFoundExceptionWithMess(){
        String equals = "edu.sumdu.tss.elephant.helper.exception.NotFoundException: Error";
        String massage = "Error";
        NotFoundException notFoundException = new NotFoundException(massage);
        assertEquals(equals,notFoundException.toString());
    }
}