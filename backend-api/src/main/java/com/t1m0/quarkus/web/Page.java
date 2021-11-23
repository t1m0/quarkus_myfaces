package com.t1m0.quarkus.web;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

@JsonDeserialize(builder = Page.Builder.class)
public class Page<T> {
    private final List<T> entries;
    private final int currentPage;
    private final int entryLimit;
    private final long totalItemCount;

    public Page(List<T> entries, int currentPage, int entryLimit, long totalItemCount) {
        this.entries = entries;
        this.currentPage = currentPage;
        this.entryLimit = entryLimit;
        this.totalItemCount = totalItemCount;
    }

    public List<T> getEntries() {
        return entries;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public int getEntryLimit() {
        return entryLimit;
    }

    public long getTotalItemCount() {
        return totalItemCount;
    }

    public int getPageCount() {
        return (int) (totalItemCount / (long) entryLimit);
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonPOJOBuilder(buildMethodName = "create", withPrefix = "set")
    public static class Builder<T> {

        private List<T> entries;
        private int currentPage;
        private int entryLimit;
        private long totalItemCount;

        public Builder<T> setEntries(List<T> entries) {
            this.entries = entries;
            return this;
        }

        public Builder<T> setCurrentPage(int currentPage) {
            this.currentPage = currentPage;
            return this;
        }

        public Builder<T> setEntryLimit(int entryLimit) {
            this.entryLimit = entryLimit;
            return this;
        }

        public Builder<T> setTotalItemCount(long totalItemCount) {
            this.totalItemCount = totalItemCount;
            return this;
        }

        public Page<T> create() {
            return new Page<>(entries, currentPage, entryLimit, totalItemCount);
        }
    }
}
