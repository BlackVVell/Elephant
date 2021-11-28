package edu.sumdu.tss.elephant.helper.utils;

import edu.sumdu.tss.elephant.helper.Keys;
import io.javalin.http.Context;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

class ResponseUtilsTest {

    Context context = Mockito.mock(Context.class);

    @Test
    void successTest() {
        String mess = "Success";
        HashMap<String, String> answer = new <String, String>HashMap<String, String>();
        answer.put("status","Ok");
        answer.put("message",mess);
        Object message = ResponseUtils.success(mess);
        assertEquals(answer.toString(),message.toString());

    }

    @Test
    void errorTest() {
        String mess = "Can't validate user";
        HashMap<String, String> answer = new <String, String>HashMap<String, String>();
        answer.put("status","Error");
        answer.put("message",mess);
        Object message = ResponseUtils.error(mess);
        assertEquals(answer.toString(),message.toString());
    }

    @Test
    void flush_flash() {
        doNothing().when(context).sessionAttribute(Keys.ERROR_KEY, null);
        ResponseUtils.flush_flash(context);
//        Mockito.verify(context, Mockito.times(1)).sessionAttribute(Keys.ERROR_KEY, null);
        verify(context, Mockito.times(1)).sessionAttribute(eq(Keys.ERROR_KEY), eq(null));
    }
}