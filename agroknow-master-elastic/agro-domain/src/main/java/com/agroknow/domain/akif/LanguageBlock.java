package com.agroknow.domain.akif;

import java.io.Serializable;
import java.util.List;

import org.hibernate.validator.constraints.NotEmpty;

public class LanguageBlock implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    @NotEmpty
    private String title;
    @NotEmpty
    private List<String> keywords;
    private String coverage;
    @NotEmpty
    private String description;

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @return the keywords
     */
    public List<String> getKeywords() {
        return keywords;
    }

    /**
     * @return the coverage
     */
    public String getCoverage() {
        return coverage;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param title
     *            the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @param keywords
     *            the keywords to set
     */
    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }

    /**
     * @param coverage
     *            the coverage to set
     */
    public void setCoverage(String coverage) {
        this.coverage = coverage;
    }

    /**
     * @param description
     *            the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }
}
