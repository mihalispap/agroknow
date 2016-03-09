
package com.agroknow.domain.agrif;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 *
 * @author aggelos
 */
public class Controlled implements Serializable{

    private static final long serialVersionUID = 1L;
    private Map<String,String> classification;
    private List<SourceValue> type;
    private List<SourceValue> spatialCoverage;
    private List<SourceValue> temporalCoverage;

    public Map<String, String> getClassification() {
        return classification;
    }

    public void setClassification(Map<String, String> classification) {
        this.classification = classification;
    }

    public List<SourceValue> getType() {
        return type;
    }

    public void setType(List<SourceValue> type) {
        this.type = type;
    }

    public List<SourceValue> getSpatialCoverage() {
        return spatialCoverage;
    }

    public void setSpatialCoverage(List<SourceValue> spatialCoverage) {
        this.spatialCoverage = spatialCoverage;
    }

    public List<SourceValue> getTemporalCoverage() {
        return temporalCoverage;
    }

    public void setTemporalCoverage(List<SourceValue> temporalCoverage) {
        this.temporalCoverage = temporalCoverage;
    }

    public static final class SourceValue implements Serializable{
        private static final long serialVersionUID = 1L;
        private String source;
        private String value;

        public String getSource() {
            return source;
        }

        public void setSource(String source) {
            this.source = source;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}
