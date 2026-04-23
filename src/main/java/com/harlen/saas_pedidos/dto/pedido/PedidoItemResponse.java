package com.harlen.saas_pedidos.dto.pedido;

import java.math.BigDecimal;

public record PedidoItemResponse(
    Long id,
    Long produtoId,
    String produtoNome,
    Integer quantidade,
    BigDecimal precoUnitario,
    BigDecimal subtotal
) {
}
