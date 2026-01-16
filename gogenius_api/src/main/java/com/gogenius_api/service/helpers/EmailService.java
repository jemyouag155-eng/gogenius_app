package com.gogenius_api.service.helpers;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
private final JavaMailSender mailSender;
    
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }
    
    public void sendResetPasswordEmail(String to, String token) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Réinitialisation de mot de passe");
        message.setText(
            "Bonjour,\n\n" +
            "Vous avez demandé la réinitialisation de votre mot de passe.\n\n" +
            "Votre code de réinitialisation est : " + token + "\n\n" +
            "Ce code expire dans 1 heure.\n\n" +
            "Si vous n'avez pas demandé cette réinitialisation, ignorez cet email.\n\n" +
            "Cordialement,\n" +
            "L'équipe GoGenius"
        );
        
        mailSender.send(message);
    }
}
