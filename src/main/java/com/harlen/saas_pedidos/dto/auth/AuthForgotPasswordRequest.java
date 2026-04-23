package com.harlen.saas_pedidos.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record AuthForgotPasswordRequest(
    @NotBlank(message = "Email e obrigatorio")
    @Email(message = "Email invalido")
    String email
) {
}
