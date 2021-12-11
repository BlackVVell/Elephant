package edu.sumdu.tss.elephant.unit.helper.exception;

import edu.sumdu.tss.elephant.helper.exception.HttpException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HttpExceptionTest {

    @Test
    void getCode() {

        HttpException httpException = new HttpException();
        int code = 500;
        assertEquals(httpException.getCode(),code);

    }

    @Test
    void getIcon() {
        HttpException httpException = new HttpException();
        String defaultStr = "bug";

        assertEquals(httpException.getIcon(),defaultStr);

    }

    @Test
    void HttpExceptionWithEx(){
        NullPointerException exception = new NullPointerException();
        HttpException httpException = new HttpException(exception);

        assertEquals(exception, httpException.getCause());
    }

    @Test
    void HttpExceptionWithMess(){

        String equals = "edu.sumdu.tss.elephant.helper.exception.HttpException: Error";
        String massage = "Error";
        HttpException httpException = new HttpException(massage);

        assertEquals(equals,httpException.toString());
    }

    @Test
    void HttpExceptionWithExAndMess(){
        NullPointerException exception = new NullPointerException();
        String equals = "edu.sumdu.tss.elephant.helper.exception.HttpException: Error";
        String message = "Error";
        HttpException httpException = new HttpException(message,exception);

        assertEquals(equals,httpException.toString());
    }
}