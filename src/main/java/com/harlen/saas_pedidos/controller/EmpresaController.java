package com.harlen.saas_pedidos.controller;

import com.harlen.saas_pedidos.dto.empresa.EmpresaRequest;
import com.harlen.saas_pedidos.dto.empresa.EmpresaResponse;
import com.harlen.saas_pedidos.service.EmpresaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/empresas")
public class EmpresaController {

    private final EmpresaService empresaService;

    public EmpresaController(EmpresaService empresaService) {
        this.empresaService = empresaService;
    }

    @GetMapping("/me")
    public EmpresaResponse buscarEmpresaAtual() {
        return empresaService.buscarEmpresaAtual();
    }

    @PutMapping("/me")
    public EmpresaResponse atualizarEmpresaAtual(@Valid @RequestBody EmpresaRequest empresaRequest) {
        return empresaService.atualizarEmpresaAtual(empresaRequest);
    }

    @DeleteMapping("/me")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluirEmpresaAtual() {
        empresaService.excluirEmpresaAtual();
    }
}
