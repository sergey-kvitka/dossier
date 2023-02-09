package com.kvitka.dossier.enums;

import com.kvitka.dossier.constants.TopicNames;

public enum Theme {
    FINISH_REGISTRATION(TopicNames.FINISH_REGISTRATION),
    CREATE_DOCUMENTS(TopicNames.CREATE_DOCUMENTS),
    SEND_DOCUMENTS(TopicNames.SEND_DOCUMENTS),
    SEND_SES(TopicNames.SEND_SES),
    CREDIT_ISSUED(TopicNames.CREDIT_ISSUED),
    APPLICATION_DENIED(TopicNames.APPLICATION_DENIED);

    private final String topic;

    Theme(String topic) {
        this.topic = topic;
    }

    public String topic() {
        return topic;
    }
}
