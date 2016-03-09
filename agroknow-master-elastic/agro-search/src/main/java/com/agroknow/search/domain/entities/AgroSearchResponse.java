package com.agroknow.search.domain.entities;

import com.agroknow.search.domain.entities.AgroSearchResponse.TermsFacet.FacetTerm;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.elasticsearch.search.facet.Facets;

/**
 * AgroSearchResponse holds the response of search results as needed for
 * agroknow api. The class is not thread-safe as it is not needed in the current
 * version.
 *
 * @author aggelos
 * @param <T>
 */
public class AgroSearchResponse<T> {

    private long total = 0;
    private long time = -1;
    private int page = -1;
    private int pageSize = -1;
    private String sortByField;
    private String sortOrder = "asc";
    private Map<String, Facet> facets = new HashMap<String, Facet>(5);
    private Collection<T> results = new ArrayList<T>(10);

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public void increaseTotal() {
        this.increaseTotal(1);
    }

    public void increaseTotal(long num) {
        this.total += num;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
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

    public Map<String, Facet> getFacets() {
        return facets;
    }

    public void setFacets(Map<String, Facet> facets) {
        this.facets = facets;
    }

    public Collection<T> getResults() {
        return results;
    }

    public void addResult(T result) {
        results.add(result);
    }

    public void setResults(Collection<T> results) {
        this.results = results;
    }

    //TODO: solve jackson problem serializing elastisearch.Facet instead
    //of replicating the structure here and creating all that mapping code
    //SERIOUSLY: read the above TODO
    public void setupFacetsFromES(Facets facets) {
        // some temp references
        TermsFacet tf;
        DatesFacet df;

        for (org.elasticsearch.search.facet.Facet esF : facets) {
            if ("terms".equalsIgnoreCase(esF.getType())) {
                org.elasticsearch.search.facet.terms.TermsFacet esTf = (org.elasticsearch.search.facet.terms.TermsFacet) esF;
                this.facets.put(esF.getName(), (tf = new TermsFacet(esTf.getName(), esTf.getMissingCount(), esTf.getTotalCount(), esTf.getOtherCount())));
                for (org.elasticsearch.search.facet.terms.TermsFacet.Entry esE : esTf.getEntries()) {
                    tf.addTerm(new FacetTerm(esE.getTerm().string(), esE.getCount()));
                }
            } else if ("date_histogram".equalsIgnoreCase(esF.getType())) {
                org.elasticsearch.search.facet.datehistogram.DateHistogramFacet esDf = (org.elasticsearch.search.facet.datehistogram.DateHistogramFacet) esF;
                this.facets.put(esF.getName(), (df = new DatesFacet(esDf.getName())));
                for (org.elasticsearch.search.facet.datehistogram.DateHistogramFacet.Entry esE : esDf.getEntries()) {
                    df.addDate(new DatesFacet.FacetDate(esE.getTime(), esE.getCount(), esE.getMean(), esE.getMin(), esE.getMax(), esE.getTotal(), esE.getTotalCount()));
                }
            }
        }
    }

    public static class Facet {

        protected String name;
        protected String _type;

        public String get_type() {
            return _type;
        }
    }

    public static class TermsFacet extends AgroSearchResponse.Facet {

        private long missing;
        private long total;
        private long other;
        private Collection<FacetTerm> terms = new ArrayList<FacetTerm>(5);

        public TermsFacet() {
            this(null, 0, 0, 0);
        }

        public TermsFacet(String name, long missing, long total, long other) {
            _type = "terms";
            this.name = name;
            this.missing = missing;
            this.total = total;
            this.other = other;
        }

        public long getMissing() {
            return missing;
        }

        public void setMissing(long missing) {
            this.missing = missing;
        }

        public long getTotal() {
            return total;
        }

        public void setTotal(long total) {
            this.total = total;
        }

        public long getOther() {
            return other;
        }

        public void setOther(long other) {
            this.other = other;
        }

        public Collection<FacetTerm> getTerms() {
            return terms;
        }

        public void setTerms(Collection<FacetTerm> terms) {
            this.terms = terms;
        }

        public void addTerm(FacetTerm term) {
            this.terms.add(term);
        }

        public static class FacetTerm {

            private String term;
            private int count;

            public FacetTerm() {
            }

            public FacetTerm(String term, int count) {
                this.term = term;
                this.count = count;
            }

            public String getTerm() {
                return term;
            }

            public void setTerm(String term) {
                this.term = term;
            }

            public int getCount() {
                return count;
            }

            public void setCount(int count) {
                this.count = count;
            }
        }
    }

    public static class DatesFacet extends AgroSearchResponse.Facet {

        private Collection<FacetDate> dates = new ArrayList<FacetDate>(5);

        public DatesFacet() {
            this(null);
        }

        public DatesFacet(String name) {
            _type = "dates";
            this.name = name;
        }

        public Collection<FacetDate> getDates() {
            return dates;
        }

        public void setDates(Collection<FacetDate> dates) {
            this.dates = dates;
        }

        public void addDate(FacetDate date) {
            this.dates.add(date);
        }

        public static class FacetDate {

            private long time;
            private long count;
            private double mean;
            private double min;
            private double max;
            private long totalCount;
            private double total;

            public FacetDate() {
            }

            public FacetDate(long time, long count, double mean, double min, double max, double total, long totalCount) {
                this.time = time;
                this.count = count;
                this.mean = mean;
                this.min = min;
                this.max = max;
                this.total = total;
                this.totalCount = totalCount;
            }

            public long getTime() {
                return time;
            }

            public void setTime(long time) {
                this.time = time;
            }

            public long getCount() {
                return count;
            }

            public void setCount(long count) {
                this.count = count;
            }

            public double getMean() {
                return mean;
            }

            public void setMean(double mean) {
                this.mean = mean;
            }

            public double getMin() {
                return min;
            }

            public void setMin(double min) {
                this.min = min;
            }

            public double getMax() {
                return max;
            }

            public void setMax(double max) {
                this.max = max;
            }

            public long getTotalCount() {
                return totalCount;
            }

            public void setTotalCount(long totalCount) {
                this.totalCount = totalCount;
            }

            public double getTotal() {
                return total;
            }

            public void setTotal(double total) {
                this.total = total;
            }
        }
    }
}
