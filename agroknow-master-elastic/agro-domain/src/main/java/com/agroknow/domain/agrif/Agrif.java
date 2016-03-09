package com.agroknow.domain.agrif;

import com.agroknow.domain.InternalFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;

public class Agrif extends InternalFormat {

    private static final long serialVersionUID = 1L;

    private Map<String, LanguageBlock> languageBlocks;
    private List<Right> rights;
    private List<Expression> expressions;
    private List<Relation> relations;
    private List<Creator> creators;
    private Controlled controlled;

    public Agrif() {
    }

    public Agrif(Set<String> locations, Set<String> identifiers) {
        super(locations, identifiers);
    }

    /*
     * (non-Javadoc)
     * @see com.agroknow.domain.SimpleMetadata#getLocations()
     */
    @Override
    @JsonIgnore
    public Set<String> getLocations() {
        if (CollectionUtils.isEmpty(this.expressions))
            return null;

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

    public String getAgrifIdentifier() {
        return super.getIdentifier();
    }

    public void setAgrifIdentifier(String agrifIdentifier) {
        this.setIdentifier(agrifIdentifier);
    }

    public Map<String, LanguageBlock> getLanguageBlocks() {
        return languageBlocks;
    }

    public void setLanguageBlocks(Map<String, LanguageBlock> languageBlocks) {
        this.languageBlocks = languageBlocks;
    }

    public List<Right> getRights() {
        return rights;
    }

    public void setRights(List<Right> rights) {
        this.rights = rights;
    }

    public List<Expression> getExpressions() {
        return expressions;
    }

    public void setExpressions(List<Expression> expressions) {
        this.expressions = expressions;
    }

    public List<Relation> getRelations() {
        return relations;
    }

    public void setRelations(List<Relation> relations) {
        this.relations = relations;
    }

    public List<Creator> getCreators() {
        return creators;
    }

    public void setCreators(List<Creator> creators) {
        this.creators = creators;
    }

    public Controlled getControlled() {
        return controlled;
    }

    public void setControlled(Controlled controlled) {
        this.controlled = controlled;
    }

}
