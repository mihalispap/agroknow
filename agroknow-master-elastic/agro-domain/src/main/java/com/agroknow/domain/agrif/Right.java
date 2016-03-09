package com.agroknow.domain.agrif;

import java.io.Serializable;
import java.util.Map;

public class Right implements Serializable {

    private static final long serialVersionUID = 1L;

    private String identifier;
    private Map<String, RightInfo> rightInfo;

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public Map<String, RightInfo> getRightInfo() {
        return rightInfo;
    }

    public void setRightInfo(Map<String, RightInfo> rightInfo) {
        this.rightInfo = rightInfo;
    }

    public static final class RightInfo {
        private String rightsStatement;
        private String termsOfUse;

        public String getRightsStatement() {
            return rightsStatement;
        }

        public void setRightsStatement(String rightsStatement) {
            this.rightsStatement = rightsStatement;
        }

        public String getTermsOfUse() {
            return termsOfUse;
        }

        public void setTermsOfUse(String termsOfUse) {
            this.termsOfUse = termsOfUse;
        }
    }
}
