package com.agroknow.domain.agrif;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class Manifestation implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<Item> items;
    private String doi;
    private String isbn;
    private List<String> formats;
    private String manifestationType;
    private Map<String, String> description;
    private String duration;
    private String size;

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public String getDoi() {
        return doi;
    }

    public void setDoi(String doi) {
        this.doi = doi;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public List<String> getFormats() {
        return formats;
    }

    public void setFormats(List<String> formats) {
        this.formats = formats;
    }

    public String getManifestationType() {
        return manifestationType;
    }

    public void setManifestationType(String manifestationType) {
        this.manifestationType = manifestationType;
    }

    public Map<String, String> getDescription() {
        return description;
    }

    public void setDescription(Map<String, String> description) {
        this.description = description;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public static final class Item {
        private String location;
        private String number;
        private String url;
        private boolean broken;

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public String getNumber() {
            return number;
        }

        public void setNumber(String number) {
            this.number = number;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public boolean isBroken() {
            return broken;
        }

        public void setBroken(boolean broken) {
            this.broken = broken;
        }
    }
}