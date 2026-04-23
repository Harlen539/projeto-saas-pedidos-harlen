package com.harlen.saas_pedidos.service;

import com.harlen.saas_pedidos.dto.produto.ProdutoRequest;
import com.harlen.saas_pedidos.dto.produto.ProdutoResponse;
import com.harlen.saas_pedidos.entity.Empresa;
import com.harlen.saas_pedidos.entity.Produto;
import com.harlen.saas_pedidos.exception.ResourceNotFoundException;
import com.harlen.saas_pedidos.repository.ProdutoRepository;
import com.harlen.saas_pedidos.security.CurrentUserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProdutoService {

    private final ProdutoRepository produtoRepository;
    private final EmpresaService empresaService;
    private final CurrentUserService currentUserService;

    public ProdutoService(
        ProdutoRepository produtoRepository,
        EmpresaService empresaService,
        CurrentUserService currentUserService
    ) {
        this.produtoRepository = produtoRepository;
        this.empresaService = empresaService;
        this.currentUserService = currentUserService;
    }

    public ProdutoResponse criar(ProdutoRequest request) {
        Empresa empresa = empresaService.buscarEntidade(currentUserService.getCurrentEmpresaId());

        Produto produto = new Produto();
        produto.setNome(request.nome().trim());
        produto.setPreco(request.preco());
        produto.setEmpresa(empresa);

        return toResponse(produtoRepository.save(produto));
    }

    @Transactional(readOnly = true)
    public List<ProdutoResponse> listar() {
        return produtoRepository.findAllByEmpresaIdOrderByNomeAsc(currentUserService.getCurrentEmpresaId())
            .stream()
            .map(this::toResponse)
            .toList();
    }

    @Transactional(readOnly = true)
    public ProdutoResponse buscarPorId(Long id) {
        return toResponse(buscarEntidadeDaEmpresaAtual(id));
    }

    public ProdutoResponse atualizar(Long id, ProdutoRequest request) {
        Produto produto = buscarEntidadeDaEmpresaAtual(id);
        produto.setNome(request.nome().trim());
        produto.setPreco(request.preco());
        return toResponse(produtoRepository.save(produto));
    }

    public void excluir(Long id) {
        produtoRepository.delete(buscarEntidadeDaEmpresaAtual(id));
    }

    @Transactional(readOnly = true)
    public Produto buscarEntidadeDaEmpresaAtual(Long id) {
        return produtoRepository.findByIdAndEmpresaId(id, currentUserService.getCurrentEmpresaId())
            .orElseThrow(() -> new ResourceNotFoundException("Produto nao encontrado"));
    }

    private ProdutoResponse toResponse(Produto produto) {
        return new ProdutoResponse(produto.getId(), produto.getNome(), produto.getPreco());
    }
}
