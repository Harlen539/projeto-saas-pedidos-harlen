package com.harlen.saas_pedidos.security;

import com.harlen.saas_pedidos.entity.Role;
import com.harlen.saas_pedidos.entity.Usuario;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class AuthenticatedUser implements UserDetails {

    private final Long id;
    private final Long empresaId;
    private final String nome;
    private final String email;
    private final String senha;
    private final Role role;

    public AuthenticatedUser(Usuario usuario) {
        this.id = usuario.getId();
        this.empresaId = usuario.getEmpresa().getId();
        this.nome = usuario.getNome();
        this.email = usuario.getEmail();
        this.senha = usuario.getSenha();
        this.role = usuario.getRole();
    }

    public Long getId() {
        return id;
    }

    public Long getEmpresaId() {
        return empresaId;
    }

    public String getNome() {
        return nome;
    }

    public Role getRole() {
        return role;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getPassword() {
        return senha;
    }

    @Override
    public String getUsername() {
        return email;
    }
}
