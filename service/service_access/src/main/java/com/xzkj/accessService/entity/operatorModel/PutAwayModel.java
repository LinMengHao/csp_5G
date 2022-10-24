package com.xzkj.accessService.entity.operatorModel;

import java.io.Serializable;

/**
 * 上架信息
 */
public class PutAwayModel implements Serializable {
    private String testReportUrl;
    private String putAwayExplain;
    private String chatbotId;

    public PutAwayModel() {
    }

    public String getTestReportUrl() {
        return testReportUrl;
    }

    public void setTestReportUrl(String testReportUrl) {
        this.testReportUrl = testReportUrl;
    }

    public String getPutAwayExplain() {
        return putAwayExplain;
    }

    public void setPutAwayExplain(String putAwayExplain) {
        this.putAwayExplain = putAwayExplain;
    }

    public String getChatbotId() {
        return chatbotId;
    }

    public void setChatbotId(String chatbotId) {
        this.chatbotId = chatbotId;
    }
}
