package com.harlen.saas_pedidos.dto.pedido;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.time.LocalDate;
import java.util.List;

public record PedidoRequest(
    LocalDate data,

    @Valid
    @NotEmpty(message = "Pedido deve ter ao menos um item")
    List<PedidoItemRequest> itens
) {
}
