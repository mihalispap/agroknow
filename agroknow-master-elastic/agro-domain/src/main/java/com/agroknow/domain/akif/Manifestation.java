package com.agroknow.domain.akif;

import java.io.Serializable;
import java.util.List;

public class Manifestation implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final class Item {
        private boolean broken;
        private String url;

        /**
         * @return the broken
         */
        public boolean isBroken() {
            return broken;
        }

        /**
         * @return the url
         */
        public String getUrl() {
            return url;
        }

        /**
         * @param broken
         *            the broken to set
         */
        public void setBroken(boolean broken) {
            this.broken = broken;
        }

        /**
         * @param url
         *            the url to set
         */
        public void setUrl(String url) {
            this.url = url;
        }
    }

    private List<Item> items;
    private String language;

    /**
     * @return the items
     */
    public List<Item> getItems() {
        return items;
    }

    /**
     * @return the language
     */
    public String getLanguage() {
        return language;
    }

    /**
     * @param items
     *            the items to set
     */
    public void setItems(List<Item> items) {
        this.items = items;
    }

    /**
     * @param language
     *            the language to set
     */
    public void setLanguage(String language) {
        this.language = language;
    }
}