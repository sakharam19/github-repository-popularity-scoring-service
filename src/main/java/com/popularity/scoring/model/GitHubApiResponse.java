package com.popularity.scoring.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class GitHubApiResponse {
    @JsonProperty("total_count")
    private int totalCount;
    @JsonProperty("items")
    private List<RepositoryItems> items;

    public GitHubApiResponse() {
    }

    public GitHubApiResponse(int totalCount, List<RepositoryItems> items) {
        this.totalCount = totalCount;
        this.items = items;
    }

    public void setItems(List<RepositoryItems> items) {
        this.items = items;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public List<RepositoryItems> getItems() {
        return items;
    }
}
