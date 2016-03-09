
package com.agroknow.search.domain;

/**
 *
 * @author aggelos
 */
public class UserQuery {

    private long timestamp;
    private String query;
    private long total;

    public UserQuery() {
        this(System.currentTimeMillis(), null, 0);
    }

    public UserQuery(String query, long total) {
        this(System.currentTimeMillis(), query, total);
    }

    public UserQuery(long timestamp, String query, long total) {
        this.timestamp = timestamp;
        this.query = query;
        this.total = total;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

}
