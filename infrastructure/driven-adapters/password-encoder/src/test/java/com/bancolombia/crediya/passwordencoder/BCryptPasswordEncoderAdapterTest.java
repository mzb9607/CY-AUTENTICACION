package com.bancolombia.crediya.passwordencoder;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BCryptPasswordEncoderAdapterTest {

    private BCryptPasswordEncoderAdapter encoderAdapter;

    @BeforeEach
    void setUp() {
        encoderAdapter = new BCryptPasswordEncoderAdapter();
    }

    @Test
    void encode_ShouldReturnEncodedPassword() {
        String rawPassword = "mySecretPassword";
        String encodedPassword = encoderAdapter.encode(rawPassword);

        assertNotNull(encodedPassword);
        assertFalse(encodedPassword.isEmpty());
        // BCrypt hashes start with $2a$, $2b$, or $2y$
        assertTrue(encodedPassword.startsWith("$2a$") || encodedPassword.startsWith("$2b$") || encodedPassword.startsWith("$2y$"));
        // A valid BCrypt hash should have a specific length, typically 60 characters
        assertEquals(60, encodedPassword.length());
    }

    @Test
    void matches_ShouldReturnTrueForCorrectPassword() {
        String rawPassword = "mySecretPassword";
        String encodedPassword = encoderAdapter.encode(rawPassword);

        assertTrue(encoderAdapter.matches(rawPassword, encodedPassword));
    }

    @Test
    void matches_ShouldReturnFalseForIncorrectPassword() {
        String rawPassword = "mySecretPassword";
        String incorrectPassword = "wrongPassword";
        String encodedPassword = encoderAdapter.encode(rawPassword);

        assertFalse(encoderAdapter.matches(incorrectPassword, encodedPassword));
    }

    @Test
    void matches_ShouldReturnFalseForDifferentEncodedPassword() {
        String rawPassword = "mySecretPassword";
        String encodedPassword1 = encoderAdapter.encode(rawPassword);
        String encodedPassword2 = encoderAdapter.encode("anotherPassword"); // Encode a different password

        assertFalse(encoderAdapter.matches(rawPassword, encodedPassword2));
    }
}
