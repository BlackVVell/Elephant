package edu.sumdu.tss.elephant.unit.helper.utils;

import edu.sumdu.tss.elephant.helper.utils.StringUtils;
import edu.sumdu.tss.elephant.helper.utils.ValidatorHelper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
/**
 * ValidatorHelperTest
 */
class ValidatorHelperTest {

    @Test
    void isValidPasswordTest(){
          String password_1 = "Helloword.123";
          String password_2 = "Helloword#123";
          String password_3 = "hellOword/123";
          boolean valid_1 = ValidatorHelper.isValidPassword(password_1);
          boolean valid_2 = ValidatorHelper.isValidPassword(password_2);
          boolean valid_3 = ValidatorHelper.isValidPassword(password_3);
          assertTrue(valid_1);
          assertTrue(valid_2);
          assertTrue(valid_3);
    }

    @Test
    void isValidPassword_EmptyStringTest(){
        String password = "";
        boolean valid = ValidatorHelper.isValidPassword(password);
        assertFalse(valid);
    }

    @Test
    void isValidPassword_Missing_OneNumberTest(){
        String password = "Qwertyui/";
        boolean valid = ValidatorHelper.isValidPassword(password);
        assertFalse(valid);
    }

    @Test
    void isValidPassword_Missing_OneUpperCaseLetterTest(){
        String password = "q3wertyui/";
        boolean valid = ValidatorHelper.isValidPassword(password);
        assertFalse(valid);
    }

    @Test
    void isValidPassword_LengthTooLong(){
        String password = "q3wWrppeeedddddddddddddtpr/";
        boolean valid = ValidatorHelper.isValidPassword(password);
        assertFalse(valid);
    }

    @Test
    void isValidMailTest(){
        String email = StringUtils.randomAlphaString(8) + "@example.com";
        boolean validEmail = ValidatorHelper.isValidMail(email);
        assertTrue(validEmail);
    }

    @Test
    void isValidMail_EmptyStringTestTest(){
        String email = "";
        boolean validEmail = ValidatorHelper.isValidMail(email);
        assertFalse(validEmail);
    }

    @Test
    void isValidMail_Missing_atTest(){
        String email = StringUtils.randomAlphaString(8) + "example.com";
        boolean validEmail = ValidatorHelper.isValidMail(email);
        assertFalse(validEmail);
    }

    @Test
    void isValidMail_Missing_comTest(){
        String email = StringUtils.randomAlphaString(8) + "@";
        boolean validEmail = ValidatorHelper.isValidMail(email);
        assertFalse(validEmail);
    }



}
