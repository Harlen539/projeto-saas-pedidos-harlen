package com.harlen.saas_pedidos.dto.auth;

import jakarta.validation.constraints.NotBlank;

public record AuthRefreshRequest(
    @NotBlank(message = "Refresh token e obrigatorio")
    String refreshToken
) {
}
