package com.agroknow.domain.akif;

import java.io.Serializable;
import java.util.Map;

public class Right implements Serializable {

    private static final long serialVersionUID = 1L;

    private String url;
    private Map<String, String> description;

    /**
     * @return the url
     */
    public String getUrl() {
        return url;
    }

    /**
     * @return the description
     */
    public Map<String, String> getDescription() {
        return description;
    }

    /**
     * @param url
     *            the url to set
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * @param description
     *            the description to set
     */
    public void setDescription(Map<String, String> description) {
        this.description = description;
    }

}
