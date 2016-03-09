package com.agroknow.domain.akif;

import com.agroknow.domain.InternalFormat;
import com.agroknow.domain.validation.NotEmptyKey;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.apache.commons.collections.CollectionUtils;
import org.hibernate.validator.constraints.NotEmpty;

public class Akif extends InternalFormat {

    private static final long serialVersionUID = 1L;

    private boolean generateThumbnail;
    private List<Contributor> contributors;
    @NotNull @Valid
    private TokenBlock tokenBlock;
    @NotEmpty @NotEmptyKey @Valid
    private Map<String, LanguageBlock> languageBlocks;
    private Right rights;
    private List<Expression> expressions;
    private Map<String, List<String>> learningObjectives;

    public Akif() {}

    public Akif(Set<String> locations, Set<String> identifiers) {
        super(locations, identifiers);
    }

    /**
     * @return the rights
     */
    public Right getRights() {
        return rights;
    }

    /**
     * @param rights
     *            the rights to set
     */
    public void setRights(Right rights) {
        this.rights = rights;
    }

    /**
     * @return the learningObjectives
     */
    public Map<String, List<String>> getLearningObjectives() {
        return learningObjectives;
    }

    /**
     * @param learningObjectives
     *            the learningObjectives to set
     */
    public void setLearningObjectives(Map<String, List<String>> learningObjectives) {
        this.learningObjectives = learningObjectives;
    }

    /*
     * (non-Javadoc)
     * @see com.agroknow.domain.SimpleMetadata#getLocations()
     */
    @Override
    @JsonIgnore
    public Set<String> getLocations() {
        if (CollectionUtils.isEmpty(this.expressions)) {
            return null;
        }

        Set<String> locations = new HashSet<String>();
        for (Expression e : expressions) {
            if (CollectionUtils.isEmpty(e.getManifestations())) {
                continue;
            }
            for (Manifestation mf : e.getManifestations()) {
                if (CollectionUtils.isEmpty(mf.getItems())) {
                    continue;
                }
                for (Manifestation.Item i : mf.getItems()) {
                    if (i.getUrl() != null) {
                        locations.add(i.getUrl());
                    }
                }
            }
        }
        return locations;
    }

    /**
     * @return the generateThumbnail
     */
    public boolean isGenerateThumbnail() {
        return generateThumbnail;
    }

    /**
     * @return the contributors
     */
    public List<Contributor> getContributors() {
        return contributors;
    }

    /**
     * @return the tokenBlock
     */
    public TokenBlock getTokenBlock() {
        return tokenBlock;
    }

    /**
     * @return the languageBlocks
     */
    public Map<String, LanguageBlock> getLanguageBlocks() {
        return languageBlocks;
    }

    /**
     * @return the expressions
     */
    public List<Expression> getExpressions() {
        return expressions;
    }

    /**
     * @param generateThumbnail
     *            the generateThumbnail to set
     */
    public void setGenerateThumbnail(boolean generateThumbnail) {
        this.generateThumbnail = generateThumbnail;
    }

    /**
     * @param contributors
     *            the contributors to set
     */
    public void setContributors(List<Contributor> contributors) {
        this.contributors = contributors;
    }

    /**
     * @param tokenBlock
     *            the tokenBlock to set
     */
    public void setTokenBlock(TokenBlock tokenBlock) {
        this.tokenBlock = tokenBlock;
    }

    /**
     * @param languageBlocks
     *            the languageBlocks to set
     */
    public void setLanguageBlocks(Map<String, LanguageBlock> languageBlocks) {
        this.languageBlocks = languageBlocks;
    }

    /**
     * @param expressions
     *            the expressions to set
     */
    public void setExpressions(List<Expression> expressions) {
        this.expressions = expressions;
    }
}
