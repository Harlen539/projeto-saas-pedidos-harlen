package com.harlen.saas_pedidos.dto.auth;

import com.harlen.saas_pedidos.dto.empresa.EmpresaResponse;
import com.harlen.saas_pedidos.dto.usuario.UsuarioResponse;

public record AuthResponse(
    String token,
    String refreshToken,
    String tipo,
    UsuarioResponse usuario,
    EmpresaResponse empresa
) {
}
