package com.agroknow.domain.akif;

import com.agroknow.domain.parser.json.CustomJsonDateDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.io.Serializable;
import java.util.Date;

public class Contributor implements Serializable{
    private static final long serialVersionUID = 1L;
    private String organization;
    private String name;
    private String role;
    private Date date;

    /**
     * @return the organization
     */
    public String getOrganization() {
        return organization;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the role
     */
    public String getRole() {
        return role;
    }

    /**
     * @return the date
     */
    public Date getDate() {
        return date;
    }

    /**
     * @param organization
     *            the organization to set
     */
    public void setOrganization(String organization) {
        this.organization = organization;
    }

    /**
     * @param name
     *            the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @param role
     *            the role to set
     */
    public void setRole(String role) {
        this.role = role;
    }

    /**
     * @param date
     *            the date to set
     */
    @JsonDeserialize(using=CustomJsonDateDeserializer.class)
    public void setDate(Date date) {
        this.date = date;
    }
}
