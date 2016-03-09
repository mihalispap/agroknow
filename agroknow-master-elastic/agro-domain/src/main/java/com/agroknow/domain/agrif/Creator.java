
package com.agroknow.domain.agrif;

import java.io.Serializable;

/**
 *
 * @author aggelos
 */
public class Creator implements Serializable{

    private static final long serialVersionUID = 1L;
    private String type;
    private String name;
    private String identifier;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }
    
}
