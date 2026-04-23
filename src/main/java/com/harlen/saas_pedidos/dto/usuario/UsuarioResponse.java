package com.harlen.saas_pedidos.dto.usuario;

import com.harlen.saas_pedidos.entity.Role;

public record UsuarioResponse(
    Long id,
    String nome,
    String email,
    Role role,
    Long empresaId,
    String empresaNome
) {
}
