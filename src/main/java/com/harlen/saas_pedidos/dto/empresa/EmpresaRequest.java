package com.harlen.saas_pedidos.dto.empresa;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record EmpresaRequest(
    @NotBlank(message = "Nome da empresa e obrigatorio")
    @Size(max = 150, message = "Nome da empresa deve ter no maximo 150 caracteres")
    String nome,

    @NotBlank(message = "CNPJ e obrigatorio")
    @Size(max = 20, message = "CNPJ deve ter no maximo 20 caracteres")
    String cnpj
) {
}
