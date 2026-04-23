package com.harlen.saas_pedidos.dto.produto;

import java.math.BigDecimal;

public record ProdutoResponse(
    Long id,
    String nome,
    BigDecimal preco
) {
}
