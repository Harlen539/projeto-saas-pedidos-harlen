package com.harlen.saas_pedidos.service;

import com.harlen.saas_pedidos.dto.usuario.UsuarioRequest;
import com.harlen.saas_pedidos.dto.usuario.UsuarioResponse;
import com.harlen.saas_pedidos.entity.Empresa;
import com.harlen.saas_pedidos.entity.Role;
import com.harlen.saas_pedidos.entity.Usuario;
import com.harlen.saas_pedidos.exception.BusinessException;
import com.harlen.saas_pedidos.exception.ResourceNotFoundException;
import com.harlen.saas_pedidos.repository.UsuarioRepository;
import com.harlen.saas_pedidos.security.CurrentUserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final EmpresaService empresaService;
    private final PasswordEncoder passwordEncoder;
    private final CurrentUserService currentUserService;

    public UsuarioService(
        UsuarioRepository usuarioRepository,
        EmpresaService empresaService,
        PasswordEncoder passwordEncoder,
        CurrentUserService currentUserService
    ) {
        this.usuarioRepository = usuarioRepository;
        this.empresaService = empresaService;
        this.passwordEncoder = passwordEncoder;
        this.currentUserService = currentUserService;
    }

    public UsuarioResponse criar(UsuarioRequest request) {
        validarEmailDisponivel(request.email(), null);

        Empresa empresa = empresaService.buscarEntidade(currentUserService.getCurrentEmpresaId());

        Usuario usuario = new Usuario();
        usuario.setNome(request.nome().trim());
        usuario.setEmail(request.email().trim().toLowerCase());
        usuario.setSenha(passwordEncoder.encode(request.senha()));
        usuario.setRole(resolveRole(request.role()));
        usuario.setEmpresa(empresa);

        return toResponse(usuarioRepository.save(usuario));
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponse> listar() {
        return usuarioRepository.findAllByEmpresaIdOrderByNomeAsc(currentUserService.getCurrentEmpresaId())
            .stream()
            .map(this::toResponse)
            .toList();
    }

    @Transactional(readOnly = true)
    public UsuarioResponse buscarPorId(Long id) {
        return toResponse(buscarEntidadeDaEmpresaAtual(id));
    }

    public UsuarioResponse atualizar(Long id, UsuarioRequest request) {
        Usuario usuario = buscarEntidadeDaEmpresaAtual(id);
        validarEmailDisponivel(request.email(), id);

        Empresa empresa = empresaService.buscarEntidade(currentUserService.getCurrentEmpresaId());

        usuario.setNome(request.nome().trim());
        usuario.setEmail(request.email().trim().toLowerCase());
        usuario.setSenha(passwordEncoder.encode(request.senha()));
        usuario.setRole(resolveRole(request.role()));
        usuario.setEmpresa(empresa);

        return toResponse(usuarioRepository.save(usuario));
    }

    public void excluir(Long id) {
        Usuario usuario = buscarEntidadeDaEmpresaAtual(id);

        if (usuario.getRole() == Role.OWNER
            && usuarioRepository.countByEmpresaIdAndRole(currentUserService.getCurrentEmpresaId(), Role.OWNER) <= 1) {
            throw new BusinessException("Nao e possivel excluir o ultimo usuario OWNER da empresa");
        }

        usuarioRepository.delete(usuario);
    }

    private void validarEmailDisponivel(String email, Long usuarioIdAtual) {
        usuarioRepository.findByEmailIgnoreCase(email.trim())
            .ifPresent(usuario -> {
                if (usuarioIdAtual == null || !usuario.getId().equals(usuarioIdAtual)) {
                    throw new BusinessException("Ja existe usuario cadastrado com este email");
                }
            });
    }

    @Transactional(readOnly = true)
    private Usuario buscarEntidadeDaEmpresaAtual(Long id) {
        return usuarioRepository.findByIdAndEmpresaId(id, currentUserService.getCurrentEmpresaId())
            .orElseThrow(() -> new ResourceNotFoundException("Usuario nao encontrado"));
    }

    private UsuarioResponse toResponse(Usuario usuario) {
        return new UsuarioResponse(
            usuario.getId(),
            usuario.getNome(),
            usuario.getEmail(),
            usuario.getRole(),
            usuario.getEmpresa().getId(),
            usuario.getEmpresa().getNome()
        );
    }

    private Role resolveRole(Role role) {
        return role != null ? role : Role.MEMBER;
    }
}
