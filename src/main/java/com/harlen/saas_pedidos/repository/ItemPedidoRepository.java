package com.harlen.saas_pedidos.repository;

import com.harlen.saas_pedidos.entity.ItemPedido;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemPedidoRepository extends JpaRepository<ItemPedido, Long> {
}
