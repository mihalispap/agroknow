
package com.agroknow.domain.agrif;

import java.util.Date;

/**
 *
 * @author aggelos
 */
public class Publisher {

    private String name;
    private Date date;
    private String location;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

}
