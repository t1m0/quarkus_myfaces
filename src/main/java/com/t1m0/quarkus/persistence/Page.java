package com.t1m0.quarkus.persistence;

import java.util.List;

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
}
