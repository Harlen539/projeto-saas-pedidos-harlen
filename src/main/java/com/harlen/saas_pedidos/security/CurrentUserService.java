package com.harlen.saas_pedidos.security;

import com.harlen.saas_pedidos.exception.BusinessException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class CurrentUserService {

    public AuthenticatedUser getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof AuthenticatedUser user)) {
            throw new BusinessException("Usuario autenticado nao encontrado");
        }

        return user;
    }

    public Long getCurrentEmpresaId() {
        return getCurrentUser().getEmpresaId();
    }

    public Long getCurrentUsuarioId() {
        return getCurrentUser().getId();
    }
}
