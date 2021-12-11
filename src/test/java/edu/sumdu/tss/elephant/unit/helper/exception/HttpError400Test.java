package edu.sumdu.tss.elephant.unit.helper.exception;

import edu.sumdu.tss.elephant.helper.exception.HttpError400;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HttpError400Test {

    @Test
    void getCode() {
        HttpError400 httpException = new HttpError400();
        int code = 400;
        assertEquals(httpException.getCode(),code);
    }
    @Test
    void HttpError400WithEx(){
        NullPointerException exception = new NullPointerException();
        HttpError400 httpError400  = new HttpError400(exception);
        assertEquals(exception,httpError400.getCause());
    }
    @Test
    void HttpError400WithMess(){
        String equals = "edu.sumdu.tss.elephant.helper.exception.HttpError400: Error";
        String message = "Error";
        HttpError400 httpError400 = new HttpError400(message);
        assertEquals(equals,httpError400.toString());
    }
}