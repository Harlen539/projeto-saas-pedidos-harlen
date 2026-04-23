package com.harlen.saas_pedidos.service;

import com.harlen.saas_pedidos.dto.empresa.EmpresaRequest;
import com.harlen.saas_pedidos.dto.empresa.EmpresaResponse;
import com.harlen.saas_pedidos.entity.Empresa;
import com.harlen.saas_pedidos.exception.BusinessException;
import com.harlen.saas_pedidos.exception.ResourceNotFoundException;
import com.harlen.saas_pedidos.repository.EmpresaRepository;
import com.harlen.saas_pedidos.repository.PedidoRepository;
import com.harlen.saas_pedidos.repository.ProdutoRepository;
import com.harlen.saas_pedidos.repository.UsuarioRepository;
import com.harlen.saas_pedidos.security.CurrentUserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EmpresaService {

    private final EmpresaRepository empresaRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProdutoRepository produtoRepository;
    private final PedidoRepository pedidoRepository;
    private final CurrentUserService currentUserService;

    public EmpresaService(
        EmpresaRepository empresaRepository,
        UsuarioRepository usuarioRepository,
        ProdutoRepository produtoRepository,
        PedidoRepository pedidoRepository,
        CurrentUserService currentUserService
    ) {
        this.empresaRepository = empresaRepository;
        this.usuarioRepository = usuarioRepository;
        this.produtoRepository = produtoRepository;
        this.pedidoRepository = pedidoRepository;
        this.currentUserService = currentUserService;
    }

    public EmpresaResponse criar(EmpresaRequest request) {
        if (empresaRepository.existsByCnpjIgnoreCase(request.cnpj().trim())) {
            throw new BusinessException("Ja existe empresa cadastrada com este CNPJ");
        }

        Empresa empresa = new Empresa();
        empresa.setNome(request.nome().trim());
        empresa.setCnpj(request.cnpj().trim());
        return toResponse(empresaRepository.save(empresa));
    }

    public List<EmpresaResponse> listar() {
        return empresaRepository.findAll()
            .stream()
            .map(this::toResponse)
            .toList();
    }

    public EmpresaResponse buscarPorId(Long id) {
        return toResponse(buscarEntidade(id));
    }

    public EmpresaResponse atualizar(Long id, EmpresaRequest request) {
        Empresa empresa = buscarEntidade(id);
        empresa.setNome(request.nome().trim());
        empresa.setCnpj(request.cnpj().trim());
        return toResponse(empresaRepository.save(empresa));
    }

    public void excluir(Long id) {
        Empresa empresa = buscarEntidade(id);
        empresaRepository.delete(empresa);
    }

    @Transactional(readOnly = true)
    public EmpresaResponse buscarEmpresaAtual() {
        return toResponse(buscarEntidade(currentUserService.getCurrentEmpresaId()));
    }

    public EmpresaResponse atualizarEmpresaAtual(EmpresaRequest request) {
        Empresa empresa = buscarEntidade(currentUserService.getCurrentEmpresaId());
        String novoCnpj = request.cnpj().trim();

        if (!empresa.getCnpj().equalsIgnoreCase(novoCnpj) && empresaRepository.existsByCnpjIgnoreCase(novoCnpj)) {
            throw new BusinessException("Ja existe empresa cadastrada com este CNPJ");
        }

        empresa.setNome(request.nome().trim());
        empresa.setCnpj(novoCnpj);
        return toResponse(empresaRepository.save(empresa));
    }

    @Transactional
    public void excluirEmpresaAtual() {
        Long empresaId = currentUserService.getCurrentEmpresaId();

        if (pedidoRepository.countByEmpresaId(empresaId) > 0 || produtoRepository.countByEmpresaId(empresaId) > 0) {
            throw new BusinessException("Nao e possivel excluir a empresa com produtos ou pedidos cadastrados");
        }

        if (usuarioRepository.countByEmpresaId(empresaId) > 1) {
            throw new BusinessException("Nao e possivel excluir a empresa com mais de um usuario cadastrado");
        }

        usuarioRepository.deleteById(currentUserService.getCurrentUsuarioId());
        empresaRepository.deleteById(empresaId);
    }

    @Transactional(readOnly = true)
    public Empresa buscarEntidade(Long id) {
        return empresaRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Empresa nao encontrada para o id " + id));
    }

    private EmpresaResponse toResponse(Empresa empresa) {
        return new EmpresaResponse(empresa.getId(), empresa.getNome(), empresa.getCnpj());
    }
}
