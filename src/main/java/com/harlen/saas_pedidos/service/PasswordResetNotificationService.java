package com.harlen.saas_pedidos.service;

import com.harlen.saas_pedidos.entity.Usuario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class PasswordResetNotificationService {

    private static final Logger log = LoggerFactory.getLogger(PasswordResetNotificationService.class);
    private final ObjectProvider<JavaMailSender> mailSenderProvider;
    private final boolean mailEnabled;
    private final String mailFrom;
    private final String resetPasswordUrl;

    public PasswordResetNotificationService(
        ObjectProvider<JavaMailSender> mailSenderProvider,
        @Value("${app.mail.enabled:false}") boolean mailEnabled,
        @Value("${app.mail.from}") String mailFrom,
        @Value("${app.frontend.reset-password-url}") String resetPasswordUrl
    ) {
        this.mailSenderProvider = mailSenderProvider;
        this.mailEnabled = mailEnabled;
        this.mailFrom = mailFrom;
        this.resetPasswordUrl = resetPasswordUrl;
    }

    public void sendResetLink(Usuario usuario, String token) {
        String resetUrl = resetPasswordUrl + "?token=" + token;

        if (!mailEnabled) {
            log.info("Password reset token generated for user={} url={}", usuario.getEmail(), resetUrl);
            return;
        }

        JavaMailSender mailSender = mailSenderProvider.getIfAvailable();

        if (mailSender == null) {
            log.warn("Mail sending enabled but JavaMailSender is not available. Falling back to log output for user={}", usuario.getEmail());
            log.info("Password reset token generated for user={} url={}", usuario.getEmail(), resetUrl);
            return;
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(mailFrom);
        message.setTo(usuario.getEmail());
        message.setSubject("Recuperacao de senha");
        message.setText("Use este link para redefinir sua senha: " + resetUrl);
        mailSender.send(message);

        log.info("Password reset email sent to user={}", usuario.getEmail());
    }
}
