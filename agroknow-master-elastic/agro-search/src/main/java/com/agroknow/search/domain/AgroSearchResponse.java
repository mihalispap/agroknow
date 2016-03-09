package com.agroknow.search.domain;

import com.agroknow.search.domain.AgroSearchResponse.TermsFacet.FacetTerm;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.elasticsearch.search.facet.Facets;

/**
 * AgroSearchResponse holds the response of search results as needed for agroknow
 api. The class is not thread-safe as it is not needed in the current version.
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
        TermsFacet tf;
        for(org.elasticsearch.search.facet.Facet esF : facets) {
            if("terms".equalsIgnoreCase(esF.getType())) {
                org.elasticsearch.search.facet.terms.TermsFacet esTf = (org.elasticsearch.search.facet.terms.TermsFacet) esF;
                this.facets.put(esF.getName(), (tf = new TermsFacet(esTf.getName(), esTf.getMissingCount(), esTf.getTotalCount(), esTf.getOtherCount())));
                for(org.elasticsearch.search.facet.terms.TermsFacet.Entry esE : esTf.getEntries()) {
                    tf.addTerm(new FacetTerm(esE.getTerm().string(), esE.getCount()));
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

//{
//   "count":4571881,
//   "start":0,
//   "limit":1,
//   "docs":[ ... ],
//   "facets":{
//      "provider.name":{
//         "_type":"terms",
//         "missing":9,
//         "total":4571872,
//         "other":0,
//         "terms":[
//            {
//               "term":"HathiTrust",
//               "count":1699073
//            },
//            {
//               "term":"Mountain West Digital Library",
//               "count":773751
//            }
//         ]
//      }
//   }
//}
}
