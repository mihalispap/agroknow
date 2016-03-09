
package com.agroknow.search.domain.entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author aggelos
 */
public class AgroSearchRequest {

    private Map<String,String> searchFields = new HashMap<String,String>();
    private String sortByField;
    private String sortOrder = "asc";
    private List<String> facetFields = new ArrayList<String>(6);
    private int facetSize = 10;
    private int pageSize = 10;
    private int page = 1;

    public Map<String, String> getSearchFields() {
        return searchFields;
    }

    public void setSearchFields(Map<String, String> searchFields) {
        this.searchFields = searchFields;
    }

    public void addSearchFields(String field, String search) {
        this.searchFields.put(field, search);
    }

    public String getSortByField() {
        return sortByField;
    }

    public void setSortByField(String sortByField) {
        this.sortByField = sortByField;
    }

    public String getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(String sortOrder) {
        this.sortOrder = sortOrder;
    }

    public List<String> getFacetFields() {
        return facetFields;
    }

    public void setFacetFields(List<String> facetFields) {
        this.facetFields = facetFields;
    }

    public void addFacetFields(String field) {
        this.facetFields.add(field);
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getFacetSize() {
        return facetSize;
    }

    public void setFacetSize(int facetSize) {
        this.facetSize = facetSize;
    }

}
