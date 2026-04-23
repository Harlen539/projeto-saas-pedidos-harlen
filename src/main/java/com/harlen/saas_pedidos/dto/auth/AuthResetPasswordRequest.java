package com.harlen.saas_pedidos.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AuthResetPasswordRequest(
    @NotBlank(message = "Token de reset e obrigatorio")
    String token,

    @NotBlank(message = "Nova senha e obrigatoria")
    @Size(min = 6, max = 120, message = "Senha deve ter entre 6 e 120 caracteres")
    String novaSenha
) {
}
