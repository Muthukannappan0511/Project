package com.rvz.service;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender sender;

    @Value("${app.mail.from}")
    private String from;

    public EmailService(JavaMailSender sender) {
        this.sender = sender;
    }

    public void sendCredentials(String to, String tempPassword) {
        SimpleMailMessage msg = new SimpleMailMessage();
        System.out.println("hi");
         msg.setFrom(from);
        msg.setTo(to);
        msg.setSubject("Your FUSOT Account Credentials");
        msg.setText(
                "Hello,\n\nYour account has been created.\n\n" +
                "Login Email: " + to + "\n" +
                "Temporary Password: " + tempPassword + "\n\n" +
                "You must change your password on first login.\n\nThanks."
        );
        sender.send(msg);
    }
}