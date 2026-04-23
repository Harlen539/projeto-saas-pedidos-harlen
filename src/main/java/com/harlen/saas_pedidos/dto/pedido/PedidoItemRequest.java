package com.harlen.saas_pedidos.dto.pedido;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record PedidoItemRequest(
    @NotNull(message = "produtoId e obrigatorio")
    Long produtoId,

    @NotNull(message = "Quantidade e obrigatoria")
    @Min(value = 1, message = "Quantidade deve ser no minimo 1")
    Integer quantidade
) {
}
