package com.popularity.scoring.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public class RepositoryItems {
    private String name;
    @JsonProperty("full_name")
    private String fullName;
    @JsonProperty("stargazers_count")
    private int stars;
    @JsonProperty("forks_count")
    private int forks;
    @JsonProperty("updated_at")
    private LocalDateTime lastUpdated;
    @JsonProperty("created_at")
    private LocalDateTime createdDate;
    @JsonProperty("html_url")
    private String repoURL;

    public RepositoryItems() {
    }

    public RepositoryItems(String name, String fullName, int stars, int forks, LocalDateTime lastUpdated, LocalDateTime createdDate, String repoURL) {
        this.name = name;
        this.fullName = fullName;
        this.stars = stars;
        this.forks = forks;
        this.lastUpdated = lastUpdated;
        this.createdDate = createdDate;
        this.repoURL = repoURL;
    }

    public String getName() {
        return name;
    }

    public String getFullName() {
        return fullName;
    }

    public int getStars() {
        return stars;
    }

    public int getForks() {
        return forks;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setStars(int stars) {
        this.stars = stars;
    }

    public void setForks(int forks) {
        this.forks = forks;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public String getRepoURL() {
        return repoURL;
    }

    public void setRepoURL(String repoURL) {
        this.repoURL = repoURL;
    }
}
