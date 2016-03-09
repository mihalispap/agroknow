package com.agroknow.domain.agrif;

import java.io.Serializable;
import java.util.List;

public class Expression implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<String> language;
    private List<Manifestation> manifestations;
    private List<Citation> citation;
    private List<Publisher> publishers;
    private List<String> descriptionEdition;

    public List<String> getLanguage() {
        return language;
    }

    public void setLanguage(List<String> language) {
        this.language = language;
    }

    public List<Manifestation> getManifestations() {
        return manifestations;
    }

    public void setManifestations(List<Manifestation> manifestations) {
        this.manifestations = manifestations;
    }

    public List<Citation> getCitation() {
        return citation;
    }

    public void setCitation(List<Citation> citation) {
        this.citation = citation;
    }

    public List<Publisher> getPublishers() {
        return publishers;
    }

    public void setPublishers(List<Publisher> publishers) {
        this.publishers = publishers;
    }

    public List<String> getDescriptionEdition() {
        return descriptionEdition;
    }

    public void setDescriptionEdition(List<String> descriptionEdition) {
        this.descriptionEdition = descriptionEdition;
    }

    
}
