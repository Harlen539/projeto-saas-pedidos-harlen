package com.harlen.saas_pedidos.dto.empresa;

public record EmpresaResponse(
    Long id,
    String nome,
    String cnpj
) {
}
