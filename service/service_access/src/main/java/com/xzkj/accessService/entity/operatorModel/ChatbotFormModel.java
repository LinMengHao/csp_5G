package com.xzkj.accessService.entity.operatorModel;

import java.io.Serializable;

/**
 * chatbot白名单
 */
public class ChatbotFormModel implements Serializable {
    private String chatbotId;
    private String chatBotWhiteList;

    public ChatbotFormModel() {
    }

    public String getChatbotId() {
        return chatbotId;
    }

    public void setChatbotId(String chatbotId) {
        this.chatbotId = chatbotId;
    }

    public String getChatBotWhiteList() {
        return chatBotWhiteList;
    }

    public void setChatBotWhiteList(String chatBotWhiteList) {
        this.chatBotWhiteList = chatBotWhiteList;
    }
}
