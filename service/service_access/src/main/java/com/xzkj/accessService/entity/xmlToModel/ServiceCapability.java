package com.xzkj.accessService.entity.xmlToModel;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.io.Serializable;

public class ServiceCapability implements Serializable {
    private static final long serialVersionUID = 17L;
    @JacksonXmlProperty(localName = "capabilityId")
    private String capabilityId;
    @JacksonXmlProperty(localName = "version")
    private String version;

    public String getCapabilityId() {
        return capabilityId;
    }

    public void setCapabilityId(String capabilityId) {
        this.capabilityId = capabilityId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return "ServiceCapability{" +
                "capabilityId='" + capabilityId + '\'' +
                ", version='" + version + '\'' +
                '}';
    }

    public ServiceCapability() {
    }
}
