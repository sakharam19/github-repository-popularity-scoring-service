package com.popularity.scoring.model;


import java.time.LocalDateTime;

public class RepositoriesPopularityScoreDTO {

    private String fullName;
    private int stars;
    private int forks;
    private LocalDateTime lastUpdated;
    private LocalDateTime createdDate;
    private double popularityScore;
    private String repositoryURL;

    public static class Builder {
        private final RepositoriesPopularityScoreDTO repositoriesPopularityScoreDTO;

        public Builder() {
            this.repositoriesPopularityScoreDTO = new RepositoriesPopularityScoreDTO();
        }

        public Builder repositoryURL(String repositoryURL) {
            repositoriesPopularityScoreDTO.repositoryURL = repositoryURL;
            return this;
        }

        public Builder fullName(String fullName) {
            repositoriesPopularityScoreDTO.fullName = fullName;
            return this;
        }

        public Builder stars(int stars) {
            repositoriesPopularityScoreDTO.stars = stars;
            return this;
        }

        public Builder forks(int forks) {
            repositoriesPopularityScoreDTO.forks = forks;
            return this;
        }

        public Builder lastUpdated(LocalDateTime lastUpdated) {
            repositoriesPopularityScoreDTO.lastUpdated = lastUpdated;
            return this;
        }

        public Builder createdDate(LocalDateTime createdDate) {
            repositoriesPopularityScoreDTO.createdDate = createdDate;
            return this;
        }

        public Builder popularityScore(double popularityScore) {
            repositoriesPopularityScoreDTO.popularityScore = popularityScore;
            return this;
        }

        public RepositoriesPopularityScoreDTO build() {
            return repositoriesPopularityScoreDTO;
        }
    }

    public RepositoriesPopularityScoreDTO(String fullName, int stars, int forks, LocalDateTime lastUpdated, LocalDateTime createdDate, double popularityScore) {
        this.fullName = fullName;
        this.stars = stars;
        this.forks = forks;
        this.lastUpdated = lastUpdated;
        this.createdDate = createdDate;
        this.popularityScore = popularityScore;
    }

    public RepositoriesPopularityScoreDTO(){

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

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public double getPopularityScore() {
        return popularityScore;
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

    public void setPopularityScore(double popularityScore) {
        this.popularityScore = popularityScore;
    }

    public String getRepositoryURL() {
        return repositoryURL;
    }

    public void setRepositoryURL(String repositoryURL) {
        this.repositoryURL = repositoryURL;
    }
}
