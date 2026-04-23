package com.harlen.saas_pedidos;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.harlen.saas_pedidos.repository.PasswordResetTokenRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class SaasPedidosIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Test
    void deveRegistrarEmpresaELogarComJwt() throws Exception {
        MvcResult registerResult = mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "empresaNome": "Doceria A",
                      "cnpj": "11111111111111",
                      "nome": "Owner A",
                      "email": "owner.a@teste.com",
                      "senha": "123456"
                    }
                    """))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.token").isNotEmpty())
            .andExpect(jsonPath("$.refreshToken").isNotEmpty())
            .andExpect(jsonPath("$.usuario.role").value("OWNER"))
            .andReturn();

        String registerToken = extractField(registerResult, "token");

        mockMvc.perform(get("/auth/me")
                .header("Authorization", "Bearer " + registerToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.usuario.email").value("owner.a@teste.com"))
            .andExpect(jsonPath("$.empresa.nome").value("Doceria A"));

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "email": "owner.a@teste.com",
                      "senha": "123456"
                    }
                    """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token").isNotEmpty())
            .andExpect(jsonPath("$.refreshToken").isNotEmpty())
            .andExpect(jsonPath("$.usuario.role").value("OWNER"));
    }

    @Test
    void memberNaoPodeGerenciarUsuarios() throws Exception {
        String ownerToken = registerAndExtractToken("Padaria B", "22222222222222", "owner.b@teste.com");

        mockMvc.perform(post("/usuarios")
                .header("Authorization", "Bearer " + ownerToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "nome": "Membro B",
                      "email": "member.b@teste.com",
                      "senha": "123456",
                      "role": "MEMBER"
                    }
                    """))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.role").value("MEMBER"));

        MvcResult loginResult = mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "email": "member.b@teste.com",
                      "senha": "123456"
                    }
                    """))
            .andExpect(status().isOk())
            .andReturn();

        String memberToken = extractField(loginResult, "token");

        mockMvc.perform(get("/usuarios")
                .header("Authorization", "Bearer " + memberToken))
            .andExpect(status().isForbidden());
    }

    @Test
    void empresaNaoPodeAcessarDadosDeOutraEmpresa() throws Exception {
        String tokenEmpresaA = registerAndExtractToken("Mercado A", "33333333333333", "owner.c@teste.com");
        String tokenEmpresaB = registerAndExtractToken("Mercado B", "44444444444444", "owner.d@teste.com");

        MvcResult produtoResult = mockMvc.perform(post("/produtos")
                .header("Authorization", "Bearer " + tokenEmpresaA)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "nome": "Cafe",
                      "preco": 9.9
                    }
                    """))
            .andExpect(status().isCreated())
            .andReturn();

        String produtoId = extractField(produtoResult, "id");

        mockMvc.perform(get("/produtos/" + produtoId)
                .header("Authorization", "Bearer " + tokenEmpresaB))
            .andExpect(status().isNotFound());
    }

    @Test
    void deveRenovarSessaoComRefreshToken() throws Exception {
        MvcResult registerResult = mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "empresaNome": "Loja Refresh",
                      "cnpj": "55555555555555",
                      "nome": "Owner Refresh",
                      "email": "refresh@teste.com",
                      "senha": "123456"
                    }
                    """))
            .andExpect(status().isCreated())
            .andReturn();

        String refreshToken = extractField(registerResult, "refreshToken");

        mockMvc.perform(post("/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "refreshToken": "%s"
                    }
                    """.formatted(refreshToken)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token").isNotEmpty())
            .andExpect(jsonPath("$.refreshToken").isNotEmpty());
    }

    @Test
    void deveSolicitarEResetarSenha() throws Exception {
        registerAndExtractToken("Loja Reset", "66666666666666", "reset@teste.com");

        mockMvc.perform(post("/auth/forgot-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "email": "reset@teste.com"
                    }
                    """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").isNotEmpty());

        String token = passwordResetTokenRepository.findAll().getFirst().getToken();

        mockMvc.perform(post("/auth/reset-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "token": "%s",
                      "novaSenha": "654321"
                    }
                    """.formatted(token)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("Senha alterada com sucesso"));

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "email": "reset@teste.com",
                      "senha": "654321"
                    }
                    """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token").isNotEmpty());
    }

    private String registerAndExtractToken(String empresaNome, String cnpj, String email) throws Exception {
        MvcResult result = mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "empresaNome": "%s",
                      "cnpj": "%s",
                      "nome": "Owner",
                      "email": "%s",
                      "senha": "123456"
                    }
                    """.formatted(empresaNome, cnpj, email)))
            .andExpect(status().isCreated())
            .andReturn();

        return extractField(result, "token");
    }

    private String extractField(MvcResult result, String field) throws Exception {
        JsonNode node = objectMapper.readTree(result.getResponse().getContentAsString());
        String[] path = field.split("\\.");
        JsonNode current = node;

        for (String key : path) {
            current = current.get(key);
        }

        return current.asText();
    }
}
