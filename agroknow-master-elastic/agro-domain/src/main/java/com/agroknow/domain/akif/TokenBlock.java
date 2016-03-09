package com.agroknow.domain.akif;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.hibernate.validator.constraints.NotEmpty;

public class TokenBlock implements Serializable {

    private static final long serialVersionUID = 1L;
    @NotEmpty
    private List<String> learningResourceTypes;
    @NotEmpty
    private List<String> endUserRoles;
    @NotEmpty
    private List<String> contexts;
    private Map<String, List<String>> taxonPaths;
    @NotEmpty
    private String ageRange;

    /**
     * @return the learningResourceTypes
     */
    public List<String> getLearningResourceTypes() {
        return learningResourceTypes;
    }

    /**
     * @return the endUserRoles
     */
    public List<String> getEndUserRoles() {
        return endUserRoles;
    }

    /**
     * @return the contexts
     */
    public List<String> getContexts() {
        return contexts;
    }

    /**
     * @return the ageRange
     */
    public String getAgeRange() {
        return ageRange;
    }

    /**
     * @param learningResourceTypes
     *            the learningResourceTypes to set
     */
    public void setLearningResourceTypes(List<String> learningResourceTypes) {
        this.learningResourceTypes = learningResourceTypes;
    }

    /**
     * @param endUserRoles
     *            the endUserRoles to set
     */
    public void setEndUserRoles(List<String> endUserRoles) {
        this.endUserRoles = endUserRoles;
    }

    /**
     * @param contexts
     *            the contexts to set
     */
    public void setContexts(List<String> contexts) {
        this.contexts = contexts;
    }

    /**
     * @param ageRange
     *            the ageRange to set
     */
    public void setAgeRange(String ageRange) {
        this.ageRange = ageRange;
    }

    public Map<String, List<String>> getTaxonPaths() {
        return taxonPaths;
    }

    public void setTaxonPaths(Map<String, List<String>> taxonPaths) {
        this.taxonPaths = taxonPaths;
    }

}
