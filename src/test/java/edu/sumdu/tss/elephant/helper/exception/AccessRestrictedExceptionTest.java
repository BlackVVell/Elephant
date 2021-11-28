package edu.sumdu.tss.elephant.helper.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AccessRestrictedExceptionTest {

    @Test
    void AccessRestrictedException(){
        AccessRestrictedException httpException = new AccessRestrictedException();
        int code = 400;
        assertEquals(httpException.getCode(),code);
    }

    @Test
    void AccessRestrictedExceptionWithEx(){
        NullPointerException exception = new NullPointerException();
        AccessRestrictedException httpException = new AccessRestrictedException(exception);

        assertEquals(exception,httpException.getCause());
    }

    @Test
    void AccessRestrictedExceptionWithMess(){
        String equals = "edu.sumdu.tss.elephant.helper.exception.AccessRestrictedException: Error";
        String massage = "Error";
        AccessRestrictedException httpException = new AccessRestrictedException(massage);

        assertEquals(equals,httpException.toString());
    }
}