package com.xzkj.accessService.entity.xmlToModel;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.io.Serializable;

public class Link implements Serializable {
    private static final long serialVersionUID = 14L;
    @JacksonXmlProperty(localName = "rel",isAttribute = true)
    private String rel;
    @JacksonXmlProperty(localName = "href",isAttribute = true)
    private String href;

    public String getRel() {
        return rel;
    }

    public void setRel(String rel) {
        this.rel = rel;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    @Override
    public String toString() {
        return "Link{" +
                "rel='" + rel + '\'' +
                ", href='" + href + '\'' +
                '}';
    }

    public Link() {
    }
}
