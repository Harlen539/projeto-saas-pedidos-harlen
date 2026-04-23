package com.harlen.saas_pedidos.repository;

import com.harlen.saas_pedidos.entity.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;

import java.util.List;
import java.util.Optional;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    @EntityGraph(attributePaths = {"itens", "itens.produto"})
    List<Pedido> findAllByEmpresaIdOrderByDataDescIdDesc(Long empresaId);

    @EntityGraph(attributePaths = {"itens", "itens.produto"})
    Optional<Pedido> findByIdAndEmpresaId(Long id, Long empresaId);

    long countByEmpresaId(Long empresaId);
}
