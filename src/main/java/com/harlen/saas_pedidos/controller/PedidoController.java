package com.harlen.saas_pedidos.controller;

import com.harlen.saas_pedidos.dto.pedido.PedidoRequest;
import com.harlen.saas_pedidos.dto.pedido.PedidoResponse;
import com.harlen.saas_pedidos.service.PedidoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/pedidos")
public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PedidoResponse criar(@Valid @RequestBody PedidoRequest request) {
        return pedidoService.criar(request);
    }

    @GetMapping
    public List<PedidoResponse> listar() {
        return pedidoService.listar();
    }

    @GetMapping("/{id}")
    public PedidoResponse buscarPorId(@PathVariable Long id) {
        return pedidoService.buscarPorId(id);
    }

    @PutMapping("/{id}")
    public PedidoResponse atualizar(@PathVariable Long id, @Valid @RequestBody PedidoRequest request) {
        return pedidoService.atualizar(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable Long id) {
        pedidoService.excluir(id);
    }
}
