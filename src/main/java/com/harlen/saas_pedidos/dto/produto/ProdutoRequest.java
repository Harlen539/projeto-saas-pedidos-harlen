package com.harlen.saas_pedidos.dto.produto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record ProdutoRequest(
    @NotBlank(message = "Nome do produto e obrigatorio")
    @Size(max = 150, message = "Nome do produto deve ter no maximo 150 caracteres")
    String nome,

    @NotNull(message = "Preco e obrigatorio")
    @Positive(message = "Preco deve ser maior que zero")
    BigDecimal preco
) {
}
