package com.harlen.saas_pedidos.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record AuthLoginRequest(
    @NotBlank(message = "Email e obrigatorio")
    @Email(message = "Email invalido")
    String email,

    @NotBlank(message = "Senha e obrigatoria")
    String senha
) {
}
