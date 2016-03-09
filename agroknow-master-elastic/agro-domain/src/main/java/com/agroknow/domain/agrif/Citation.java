
package com.agroknow.domain.agrif;

import java.util.List;

/**
 *
 * @author aggelos
 */
public class Citation {

    private List<String> title;
    private String issn;
    private List<String> citationNumber;
    private List<String> citationChronology;

    public List<String> getTitle() {
        return title;
    }

    public void setTitle(List<String> title) {
        this.title = title;
    }

    public String getIssn() {
        return issn;
    }

    public void setIssn(String issn) {
        this.issn = issn;
    }

    public List<String> getCitationNumber() {
        return citationNumber;
    }

    public void setCitationNumber(List<String> citationNumber) {
        this.citationNumber = citationNumber;
    }

    public List<String> getCitationChronology() {
        return citationChronology;
    }

    public void setCitationChronology(List<String> citationChronology) {
        this.citationChronology = citationChronology;
    }

}
