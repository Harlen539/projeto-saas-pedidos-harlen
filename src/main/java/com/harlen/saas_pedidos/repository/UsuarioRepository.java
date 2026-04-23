package com.harlen.saas_pedidos.repository;

import com.harlen.saas_pedidos.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    @EntityGraph(attributePaths = "empresa")
    Optional<Usuario> findByEmailIgnoreCase(String email);

    @EntityGraph(attributePaths = "empresa")
    Optional<Usuario> findByIdAndEmpresaId(Long id, Long empresaId);

    @EntityGraph(attributePaths = "empresa")
    List<Usuario> findAllByEmpresaIdOrderByNomeAsc(Long empresaId);

    @EntityGraph(attributePaths = "empresa")
    Optional<Usuario> findDetailedById(Long id);

    boolean existsByEmailIgnoreCase(String email);

    long countByEmpresaId(Long empresaId);

    long countByEmpresaIdAndRole(Long empresaId, com.harlen.saas_pedidos.entity.Role role);
}
