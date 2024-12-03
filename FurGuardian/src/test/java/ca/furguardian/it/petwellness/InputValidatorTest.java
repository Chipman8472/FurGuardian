package ca.furguardian.it.petwellness;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import ca.furguardian.it.petwellness.controller.InputValidator;

class InputValidatorTest {

    @Test
    void testValidEmail() {
        assertTrue(InputValidator.isValidEmail("test@example.com"));
    }

    @Test
    void testInvalidEmail_NoAtSymbol() {
        assertFalse(InputValidator.isValidEmail("testexample.com"));
    }

    @Test
    void testInvalidEmail_NoDomain() {
        assertFalse(InputValidator.isValidEmail("test@.com"));
    }

    @Test
    void testValidPhoneNumber() {
        assertTrue(InputValidator.isValidPhoneNumber("1234567890"));
    }

    @Test
    void testInvalidPhoneNumber_TooShort() {
        assertFalse(InputValidator.isValidPhoneNumber("1234567"));
    }

    @Test
    void testInvalidPhoneNumber_ContainsLetters() {
        assertFalse(InputValidator.isValidPhoneNumber("12345abcde"));
    }

    @Test
    void testValidPassword() {
        assertTrue(InputValidator.isValidPassword("Strong1@"));
    }

    @Test
    void testInvalidPassword_NoSpecialCharacter() {
        assertFalse(InputValidator.isValidPassword("Strong123"));
    }

    @Test
    void testInvalidPassword_NoUppercaseLetter() {
        assertFalse(InputValidator.isValidPassword("weak1@word"));
    }

    @Test
    void testInvalidPassword_LengthOutOfBounds() {
        assertFalse(InputValidator.isValidPassword("Short1@")); // Too short
        assertFalse(InputValidator.isValidPassword("ThisPasswordIsWayTooLong1@")); // Too long
    }
}
