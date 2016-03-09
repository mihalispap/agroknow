package com.agroknow.domain.akif;

import java.io.Serializable;
import java.util.List;

public class Expression implements Serializable {

    private static final long serialVersionUID = 1L;

    private String language;

    private List<Manifestation> manifestations;

    /**
     * @return the language
     */
    public String getLanguage() {
        return language;
    }

    /**
     * @return the manifestations
     */
    public List<Manifestation> getManifestations() {
        return manifestations;
    }

    /**
     * @param language
     *            the language to set
     */
    public void setLanguage(String language) {
        this.language = language;
    }

    /**
     * @param manifestations
     *            the manifestations to set
     */
    public void setManifestations(List<Manifestation> manifestations) {
        this.manifestations = manifestations;
    }
}