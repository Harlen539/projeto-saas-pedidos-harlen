package com.harlen.saas_pedidos.dto.pedido;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record PedidoResponse(
    Long id,
    LocalDate data,
    BigDecimal total,
    List<PedidoItemResponse> itens
) {
}
