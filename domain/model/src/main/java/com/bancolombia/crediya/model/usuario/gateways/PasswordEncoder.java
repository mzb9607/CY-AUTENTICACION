package com.bancolombia.crediya.model.usuario.gateways;

public interface PasswordEncoder {
    String encode(String password);
    boolean matches(String rawPassword, String encodedPassword);
}
