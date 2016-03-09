package com.agroknow.domain;

import java.io.Serializable;
import java.util.Set;

public class SimpleMetadata implements Serializable{
    private static final long serialVersionUID = 1L;
    private Set<String> locations;
    private Set<String> identifiers;

    public SimpleMetadata() {
    }
    
    /**
     * @param locations
     * @param identifiers
     */
    public SimpleMetadata(Set<String> locations, Set<String> identifiers) {
        super();
        this.locations = locations;
        this.identifiers = identifiers;
    }

    public Set<String> getLocations() {
        return locations;
    }

    public void setLocations(Set<String> locations) {
        this.locations = locations;
    }

    public Set<String> getIdentifiers() {
        return identifiers;
    }

    public void setIdentifiers(Set<String> identifiers) {
        this.identifiers = identifiers;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Identifiers:\n");
        for (String identifier : identifiers) {
            sb.append(identifier + "\n");
        }
        sb.append("Locations:\n");
        for (String location : locations) {
            sb.append(location + "\n");
        }
        return sb.toString();
    }
}
