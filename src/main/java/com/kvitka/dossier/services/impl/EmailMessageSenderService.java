package com.kvitka.dossier.services.impl;

import com.kvitka.dossier.dtos.EmailMessage;
import com.kvitka.dossier.enums.Theme;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailMessageSenderService {

    @Value("${spring.mail.username}")
    private String emailFrom;

    private final JavaMailSender emailSender;

    public void sendEmailMessage(EmailMessage emailMessage) {
        SimpleMailMessage messageToSend = new SimpleMailMessage();
        messageToSend.setFrom(emailFrom);
        String emailTo = emailMessage.getAddress();
        messageToSend.setTo(emailTo);

        setTextAndSubject(messageToSend, emailMessage);
        String subject = messageToSend.getSubject();
        log.info("SimpleMailMessage created and ready to be sent (from: <{}>, to: <{}>, subject: '{}')",
                emailFrom, emailTo, subject);

        emailSender.send(messageToSend);
        log.info("SimpleMailMessage sent successfully (from: <{}>, to: <{}>, subject: '{}')",
                emailFrom, emailTo, subject);
    }

    private void setTextAndSubject(SimpleMailMessage messageToSend, EmailMessage emailMessage) {
        textAndSubjectTemplates.get(emailMessage.getTheme()).accept(messageToSend, emailMessage);
    }

    private final Map<Theme, BiConsumer<SimpleMailMessage, EmailMessage>> textAndSubjectTemplates = new HashMap<>();

    {
        textAndSubjectTemplates.put(Theme.FINISH_REGISTRATION, (simpleMailMessage, emailMessage) -> {
            simpleMailMessage.setSubject(String.format("[Loan Application #%d] Finishing registration",
                    emailMessage.getApplicationId()));
            simpleMailMessage.setText("""
                    Your application has pre-approved

                    Please finish Your registration by sending remaining personal data""");
        });

        textAndSubjectTemplates.put(Theme.CREATE_DOCUMENTS, (simpleMailMessage, emailMessage) -> {
            simpleMailMessage.setSubject(String.format("[Loan Application #%d] Creating documents",
                    emailMessage.getApplicationId()));
            simpleMailMessage.setText("""
                    Credit has calculated and Your application has approved
                                        
                    Please confirm Your request for creation of documents""");
        });

        textAndSubjectTemplates.put(Theme.SEND_DOCUMENTS, (simpleMailMessage, emailMessage) -> {
            simpleMailMessage.setSubject(String.format("[Loan Application #%d] Signing documents",
                    emailMessage.getApplicationId()));
            simpleMailMessage.setText("""
                    Documents for Your application has created
                                        
                    Please confirm Your agreement with the term of credit by signing the documents""");
        });

        textAndSubjectTemplates.put(Theme.SEND_SES, (simpleMailMessage, emailMessage) -> {
            simpleMailMessage.setSubject(String.format("[Loan Application #%d] Finishing documents signing",
                    emailMessage.getApplicationId()));
            simpleMailMessage.setText(String.format("""
                    To finish documents signing You need to verify SES code

                    Your SES code: %s

                    Please verify Your SES code to finish documents signing""", emailMessage.getInfo()));
        });

        textAndSubjectTemplates.put(Theme.CREDIT_ISSUED, (simpleMailMessage, emailMessage) -> {
            simpleMailMessage.setSubject(String.format("[Loan Application #%d] Credit issued",
                    emailMessage.getApplicationId()));
            simpleMailMessage.setText("Credit issued successfully. Thank You for choosing us");
        });

        textAndSubjectTemplates.put(Theme.APPLICATION_DENIED, (simpleMailMessage, emailMessage) -> {
            simpleMailMessage.setSubject(String.format("[Loan Application #%d] Application denied",
                    emailMessage.getApplicationId()));
            simpleMailMessage.setText("""
                    Your application has denied""");
        });
    }
}
