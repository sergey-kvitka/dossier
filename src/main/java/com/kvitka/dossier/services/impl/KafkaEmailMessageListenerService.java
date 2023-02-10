package com.kvitka.dossier.services.impl;

import com.kvitka.dossier.constants.TopicNames;
import com.kvitka.dossier.dtos.EmailMessage;
import com.kvitka.dossier.enums.ApplicationStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaEmailMessageListenerService {

    @Value("${rest.deal.url}")
    private String dealURL;

    private final String groupId = "myGroup";
    private final RestTemplate restTemplate = new RestTemplate();

    private final EmailMessageSenderService emailMessageSenderService;

    @KafkaListener(groupId = groupId, topics = {
            TopicNames.FINISH_REGISTRATION,
            TopicNames.CREATE_DOCUMENTS,
            TopicNames.SEND_SES,
            TopicNames.CREDIT_ISSUED,
            TopicNames.APPLICATION_DENIED})
    private void defaultEmailMessageListener(EmailMessage message) {
        log.info("Kafka listener: consumed EmailMessage from Kafka topic \"{}\": {}",
                message.getTheme().topic(), message);
        emailMessageSenderService.sendEmailMessage(message);
    }

    @KafkaListener(groupId = groupId, topics = TopicNames.SEND_DOCUMENTS)
    private void sendDocumentsEmailMessageListener(EmailMessage message) {
        log.info("Kafka listener: consumed EmailMessage from Kafka topic \"{}\": {}",
                message.getTheme().topic(), message);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        ApplicationStatus documentCreated = ApplicationStatus.DOCUMENT_CREATED;
        HttpEntity<ApplicationStatus> httpEntity = new HttpEntity<>(documentCreated, headers);
        String url = String.format("%s/deal/admin/application/%d/status", dealURL, message.getApplicationId());
        log.info("Sending PUT request on \"{}\" (request body: {})", url, documentCreated);
        restTemplate.put(url, httpEntity);
        log.info("PUT request on \"{}\" sent successfully!", url);

        emailMessageSenderService.sendEmailMessage(message);
    }
}
