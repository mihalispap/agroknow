package com.agroknow.domain.agrif;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.util.List;

public class LanguageBlock implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String title;
    private String alternativeTitle;
    private String titleSupplemental;
    private List<String> keywords;
    @JsonProperty("abstract") private String agrifAbstract;
    private String notes;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAlternativeTitle() {
        return alternativeTitle;
    }

    public void setAlternativeTitle(String alternativeTitle) {
        this.alternativeTitle = alternativeTitle;
    }

    public String getTitleSupplemental() {
        return titleSupplemental;
    }

    public void setTitleSupplemental(String titleSupplemental) {
        this.titleSupplemental = titleSupplemental;
    }

    public List<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }

    public String getAgrifAbstract() {
        return agrifAbstract;
    }

    public void setAgrifAbstract(String agrifAbstract) {
        this.agrifAbstract = agrifAbstract;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

}
