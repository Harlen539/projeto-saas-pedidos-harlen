package com.harlen.saas_pedidos.service;

import com.harlen.saas_pedidos.dto.pedido.PedidoItemRequest;
import com.harlen.saas_pedidos.dto.pedido.PedidoItemResponse;
import com.harlen.saas_pedidos.dto.pedido.PedidoRequest;
import com.harlen.saas_pedidos.dto.pedido.PedidoResponse;
import com.harlen.saas_pedidos.entity.Empresa;
import com.harlen.saas_pedidos.entity.ItemPedido;
import com.harlen.saas_pedidos.entity.Pedido;
import com.harlen.saas_pedidos.entity.Produto;
import com.harlen.saas_pedidos.exception.ResourceNotFoundException;
import com.harlen.saas_pedidos.repository.PedidoRepository;
import com.harlen.saas_pedidos.security.CurrentUserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final EmpresaService empresaService;
    private final ProdutoService produtoService;
    private final CurrentUserService currentUserService;

    public PedidoService(
        PedidoRepository pedidoRepository,
        EmpresaService empresaService,
        ProdutoService produtoService,
        CurrentUserService currentUserService
    ) {
        this.pedidoRepository = pedidoRepository;
        this.empresaService = empresaService;
        this.produtoService = produtoService;
        this.currentUserService = currentUserService;
    }

    @Transactional
    public PedidoResponse criar(PedidoRequest request) {
        Empresa empresa = empresaService.buscarEntidade(currentUserService.getCurrentEmpresaId());

        Pedido pedido = new Pedido();
        pedido.setEmpresa(empresa);
        pedido.setData(request.data() != null ? request.data() : LocalDate.now());
        pedido.setItens(new ArrayList<>());
        preencherItens(pedido, request.itens());

        return toResponse(pedidoRepository.save(pedido));
    }

    @Transactional(readOnly = true)
    public List<PedidoResponse> listar() {
        return pedidoRepository.findAllByEmpresaIdOrderByDataDescIdDesc(currentUserService.getCurrentEmpresaId())
            .stream()
            .map(this::toResponse)
            .toList();
    }

    @Transactional(readOnly = true)
    public PedidoResponse buscarPorId(Long id) {
        return toResponse(buscarEntidadeDaEmpresaAtual(id));
    }

    @Transactional
    public PedidoResponse atualizar(Long id, PedidoRequest request) {
        Pedido pedido = buscarEntidadeDaEmpresaAtual(id);
        pedido.setData(request.data() != null ? request.data() : pedido.getData());
        pedido.getItens().clear();
        preencherItens(pedido, request.itens());
        return toResponse(pedidoRepository.save(pedido));
    }

    public void excluir(Long id) {
        pedidoRepository.delete(buscarEntidadeDaEmpresaAtual(id));
    }

    @Transactional(readOnly = true)
    public Pedido buscarEntidadeDaEmpresaAtual(Long id) {
        return pedidoRepository.findByIdAndEmpresaId(id, currentUserService.getCurrentEmpresaId())
            .orElseThrow(() -> new ResourceNotFoundException("Pedido nao encontrado"));
    }

    private void preencherItens(Pedido pedido, List<PedidoItemRequest> itensRequest) {
        for (PedidoItemRequest itemRequest : itensRequest) {
            Produto produto = produtoService.buscarEntidadeDaEmpresaAtual(itemRequest.produtoId());

            ItemPedido item = new ItemPedido();
            item.setPedido(pedido);
            item.setProduto(produto);
            item.setQuantidade(itemRequest.quantidade());
            item.setPreco(produto.getPreco());

            pedido.getItens().add(item);
        }
    }

    private PedidoResponse toResponse(Pedido pedido) {
        List<PedidoItemResponse> itens = pedido.getItens()
            .stream()
            .map(item -> new PedidoItemResponse(
                item.getId(),
                item.getProduto().getId(),
                item.getProduto().getNome(),
                item.getQuantidade(),
                item.getPreco(),
                item.getPreco().multiply(BigDecimal.valueOf(item.getQuantidade()))
            ))
            .toList();

        BigDecimal total = itens.stream()
            .map(PedidoItemResponse::subtotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new PedidoResponse(
            pedido.getId(),
            pedido.getData(),
            total,
            itens
        );
    }
}
