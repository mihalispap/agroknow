
package com.agroknow.domain.agrif;

import java.io.Serializable;

/**
 *
 * @author aggelos
 */
public class Relation implements Serializable{

    private static final long serialVersionUID = 1L;
    private String typeOfRelation;
    private String typeOfReference;
    private String reference;

    public String getTypeOfRelation() {
        return typeOfRelation;
    }

    public void setTypeOfRelation(String typeOfRelation) {
        this.typeOfRelation = typeOfRelation;
    }

    public String getTypeOfReference() {
        return typeOfReference;
    }

    public void setTypeOfReference(String typeOfReference) {
        this.typeOfReference = typeOfReference;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    
}
