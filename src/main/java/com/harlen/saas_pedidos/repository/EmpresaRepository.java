package com.harlen.saas_pedidos.repository;

import com.harlen.saas_pedidos.entity.Empresa;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmpresaRepository extends JpaRepository<Empresa, Long> {

    boolean existsByCnpjIgnoreCase(String cnpj);
}
