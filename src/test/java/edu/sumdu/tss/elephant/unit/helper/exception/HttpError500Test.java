package edu.sumdu.tss.elephant.unit.helper.exception;

import edu.sumdu.tss.elephant.helper.exception.HttpError500;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HttpError500Test {

    @Test
    void HttpError500WithEx(){
        NullPointerException exception = new NullPointerException();
        HttpError500 httpError500 = new HttpError500(exception);
        assertEquals(exception,httpError500.getCause());
    }

    @Test
    void HttpError500WithMes(){
        String equals = "edu.sumdu.tss.elephant.helper.exception.HttpError500: Error";
        String massage = "Error";
        HttpError500 httpError500 = new HttpError500(massage);
        assertEquals(equals,httpError500.toString());
    }

    @Test
    void HttpError500WithExAndMes(){
        NullPointerException exception = new NullPointerException();
        String equals = "edu.sumdu.tss.elephant.helper.exception.HttpError500: Error";
        String message = "Error";
        HttpError500 httpError500 = new HttpError500(message,exception);
        assertEquals(equals,httpError500.toString());
    }

    @Test
    void HttpError500(){
        HttpError500 httpException = new HttpError500();
        int code = 500;
        assertEquals(httpException.getCode(),code);
    }

}