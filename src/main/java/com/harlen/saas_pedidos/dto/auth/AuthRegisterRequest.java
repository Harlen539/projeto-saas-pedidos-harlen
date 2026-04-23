package com.harlen.saas_pedidos.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AuthRegisterRequest(
    @NotBlank(message = "Nome da empresa e obrigatorio")
    @Size(max = 150, message = "Nome da empresa deve ter no maximo 150 caracteres")
    String empresaNome,

    @NotBlank(message = "CNPJ e obrigatorio")
    @Size(max = 20, message = "CNPJ deve ter no maximo 20 caracteres")
    String cnpj,

    @NotBlank(message = "Nome do usuario e obrigatorio")
    @Size(max = 150, message = "Nome do usuario deve ter no maximo 150 caracteres")
    String nome,

    @NotBlank(message = "Email e obrigatorio")
    @Email(message = "Email invalido")
    @Size(max = 150, message = "Email deve ter no maximo 150 caracteres")
    String email,

    @NotBlank(message = "Senha e obrigatoria")
    @Size(min = 6, max = 120, message = "Senha deve ter entre 6 e 120 caracteres")
    String senha
) {
}
