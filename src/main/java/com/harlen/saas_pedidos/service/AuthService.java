package com.harlen.saas_pedidos.service;

import com.harlen.saas_pedidos.dto.auth.AuthLoginRequest;
import com.harlen.saas_pedidos.dto.auth.AuthForgotPasswordRequest;
import com.harlen.saas_pedidos.dto.auth.AuthMessageResponse;
import com.harlen.saas_pedidos.dto.auth.AuthRefreshRequest;
import com.harlen.saas_pedidos.dto.auth.AuthRegisterRequest;
import com.harlen.saas_pedidos.dto.auth.AuthResetPasswordRequest;
import com.harlen.saas_pedidos.dto.auth.AuthResponse;
import com.harlen.saas_pedidos.dto.empresa.EmpresaResponse;
import com.harlen.saas_pedidos.dto.usuario.UsuarioResponse;
import com.harlen.saas_pedidos.entity.Empresa;
import com.harlen.saas_pedidos.entity.PasswordResetToken;
import com.harlen.saas_pedidos.entity.RefreshToken;
import com.harlen.saas_pedidos.entity.Role;
import com.harlen.saas_pedidos.entity.Usuario;
import com.harlen.saas_pedidos.exception.BusinessException;
import com.harlen.saas_pedidos.repository.EmpresaRepository;
import com.harlen.saas_pedidos.repository.PasswordResetTokenRepository;
import com.harlen.saas_pedidos.repository.RefreshTokenRepository;
import com.harlen.saas_pedidos.repository.UsuarioRepository;
import com.harlen.saas_pedidos.security.AuthenticatedUser;
import com.harlen.saas_pedidos.security.CurrentUserService;
import com.harlen.saas_pedidos.security.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final EmpresaRepository empresaRepository;
    private final UsuarioRepository usuarioRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final CurrentUserService currentUserService;
    private final PasswordResetNotificationService passwordResetNotificationService;

    public AuthService(
        EmpresaRepository empresaRepository,
        UsuarioRepository usuarioRepository,
        RefreshTokenRepository refreshTokenRepository,
        PasswordResetTokenRepository passwordResetTokenRepository,
        PasswordEncoder passwordEncoder,
        AuthenticationManager authenticationManager,
        JwtService jwtService,
        CurrentUserService currentUserService,
        PasswordResetNotificationService passwordResetNotificationService
    ) {
        this.empresaRepository = empresaRepository;
        this.usuarioRepository = usuarioRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.currentUserService = currentUserService;
        this.passwordResetNotificationService = passwordResetNotificationService;
    }

    @Transactional
    public AuthResponse register(AuthRegisterRequest request) {
        String email = normalizeEmail(request.email());
        String cnpj = request.cnpj().trim();

        if (empresaRepository.existsByCnpjIgnoreCase(cnpj)) {
            throw new BusinessException("Ja existe empresa cadastrada com este CNPJ");
        }

        if (usuarioRepository.existsByEmailIgnoreCase(email)) {
            throw new BusinessException("Ja existe usuario cadastrado com este email");
        }

        Empresa empresa = new Empresa();
        empresa.setNome(request.empresaNome().trim());
        empresa.setCnpj(cnpj);
        empresa = empresaRepository.save(empresa);

        Usuario usuario = new Usuario();
        usuario.setNome(request.nome().trim());
        usuario.setEmail(email);
        usuario.setSenha(passwordEncoder.encode(request.senha()));
        usuario.setRole(Role.OWNER);
        usuario.setEmpresa(empresa);
        usuario = usuarioRepository.save(usuario);

        return buildAuthResponse(usuario);
    }

    public AuthResponse login(AuthLoginRequest request) {
        String email = normalizeEmail(request.email());

        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, request.senha())
            );

            AuthenticatedUser authenticatedUser = (AuthenticatedUser) authentication.getPrincipal();
            Usuario usuario = usuarioRepository.findByIdAndEmpresaId(
                    authenticatedUser.getId(),
                    authenticatedUser.getEmpresaId()
                )
                .orElseThrow(() -> new BadCredentialsException("Credenciais invalidas"));

            return buildAuthResponse(usuario, true);
        } catch (AuthenticationException ex) {
            throw new BusinessException("Email ou senha invalidos");
        }
    }

    @Transactional(readOnly = true)
    public AuthResponse me() {
        Usuario usuario = usuarioRepository.findByIdAndEmpresaId(
                currentUserService.getCurrentUsuarioId(),
                currentUserService.getCurrentEmpresaId()
            )
            .orElseThrow(() -> new BusinessException("Usuario autenticado nao encontrado"));

        return buildAuthResponse(usuario, false);
    }

    @Transactional
    public AuthResponse refresh(AuthRefreshRequest request) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(request.refreshToken())
            .orElseThrow(() -> new BusinessException("Refresh token invalido"));

        if (refreshToken.isRevoked() || refreshToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new BusinessException("Refresh token expirado ou revogado");
        }

        Usuario usuario = refreshToken.getUsuario();
        revokeToken(refreshToken);

        return buildAuthResponse(usuario, true);
    }

    @Transactional
    public AuthMessageResponse logout(AuthRefreshRequest request) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(request.refreshToken())
            .orElseThrow(() -> new BusinessException("Refresh token invalido"));
        revokeToken(refreshToken);
        return new AuthMessageResponse("Logout realizado com sucesso");
    }

    @Transactional
    public AuthMessageResponse forgotPassword(AuthForgotPasswordRequest request) {
        usuarioRepository.findByEmailIgnoreCase(normalizeEmail(request.email()))
            .ifPresent(usuario -> {
                invalidateResetTokens(usuario.getId());

                PasswordResetToken resetToken = new PasswordResetToken();
                resetToken.setToken(UUID.randomUUID().toString());
                resetToken.setExpiresAt(LocalDateTime.now().plus(jwtService.getPasswordResetExpirationMs(), ChronoUnit.MILLIS));
                resetToken.setUsed(false);
                resetToken.setUsuario(usuario);
                passwordResetTokenRepository.save(resetToken);

                passwordResetNotificationService.sendResetLink(usuario, resetToken.getToken());
                log.info("Password reset requested for user={}", usuario.getEmail());
            });

        return new AuthMessageResponse("Se o email existir, um link de recuperacao foi gerado");
    }

    @Transactional
    public AuthMessageResponse resetPassword(AuthResetPasswordRequest request) {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(request.token())
            .orElseThrow(() -> new BusinessException("Token de reset invalido"));

        if (resetToken.isUsed() || resetToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new BusinessException("Token de reset expirado ou ja utilizado");
        }

        Usuario usuario = resetToken.getUsuario();
        usuario.setSenha(passwordEncoder.encode(request.novaSenha()));
        usuarioRepository.save(usuario);

        resetToken.setUsed(true);
        passwordResetTokenRepository.save(resetToken);
        revokeAllRefreshTokens(usuario.getId());

        return new AuthMessageResponse("Senha alterada com sucesso");
    }

    private AuthResponse buildAuthResponse(Usuario usuario) {
        return buildAuthResponse(usuario, true);
    }

    private AuthResponse buildAuthResponse(Usuario usuario, boolean issueRefreshToken) {
        AuthenticatedUser authenticatedUser = new AuthenticatedUser(usuario);
        String token = jwtService.generateToken(authenticatedUser);
        String refreshTokenValue = issueRefreshToken ? createRefreshToken(usuario).getToken() : null;

        EmpresaResponse empresaResponse = new EmpresaResponse(
            usuario.getEmpresa().getId(),
            usuario.getEmpresa().getNome(),
            usuario.getEmpresa().getCnpj()
        );

        UsuarioResponse usuarioResponse = new UsuarioResponse(
            usuario.getId(),
            usuario.getNome(),
            usuario.getEmail(),
            usuario.getRole(),
            usuario.getEmpresa().getId(),
            usuario.getEmpresa().getNome()
        );

        return new AuthResponse(token, refreshTokenValue, "Bearer", usuarioResponse, empresaResponse);
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase();
    }

    private RefreshToken createRefreshToken(Usuario usuario) {
        revokeAllRefreshTokens(usuario.getId());

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setExpiresAt(LocalDateTime.now().plus(jwtService.getRefreshExpirationMs(), ChronoUnit.MILLIS));
        refreshToken.setRevoked(false);
        refreshToken.setUsuario(usuario);
        return refreshTokenRepository.save(refreshToken);
    }

    private void revokeAllRefreshTokens(Long usuarioId) {
        refreshTokenRepository.findAllByUsuarioIdAndRevokedFalse(usuarioId)
            .forEach(this::revokeToken);
    }

    private void revokeToken(RefreshToken refreshToken) {
        refreshToken.setRevoked(true);
        refreshTokenRepository.save(refreshToken);
    }

    private void invalidateResetTokens(Long usuarioId) {
        passwordResetTokenRepository.findAllByUsuarioIdAndUsedFalse(usuarioId)
            .forEach(token -> {
                token.setUsed(true);
                passwordResetTokenRepository.save(token);
            });
    }
}
