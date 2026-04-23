package com.harlen.saas_pedidos.repository;

import com.harlen.saas_pedidos.entity.Produto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {

    List<Produto> findAllByEmpresaIdOrderByNomeAsc(Long empresaId);

    Optional<Produto> findByIdAndEmpresaId(Long id, Long empresaId);

    long countByEmpresaId(Long empresaId);
}
