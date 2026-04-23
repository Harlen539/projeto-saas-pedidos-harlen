package com.harlen.saas_pedidos.dto.usuario;

import com.harlen.saas_pedidos.entity.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UsuarioRequest(
    @NotBlank(message = "Nome do usuario e obrigatorio")
    @Size(max = 150, message = "Nome do usuario deve ter no maximo 150 caracteres")
    String nome,

    @NotBlank(message = "Email e obrigatorio")
    @Email(message = "Email invalido")
    @Size(max = 150, message = "Email deve ter no maximo 150 caracteres")
    String email,

    @NotBlank(message = "Senha e obrigatoria")
    @Size(min = 6, max = 120, message = "Senha deve ter entre 6 e 120 caracteres")
    String senha,

    Role role
) {
}
