package com.popularity.scoring.model;

import java.math.BigInteger;
import java.util.List;

public class GithubPopularityScoreResponse {

    private int totalNumberOfRepositories;
    private List<RepositoriesPopularityScoreDTO>  repositoriesPopularityScoreDTO;

    public GithubPopularityScoreResponse() {
    }

    public GithubPopularityScoreResponse(int totalNumberOfRepositories, List<RepositoriesPopularityScoreDTO> repositoriesPopularityScoreDTO) {
        this.totalNumberOfRepositories = totalNumberOfRepositories;
        this.repositoriesPopularityScoreDTO = repositoriesPopularityScoreDTO;
    }

    public int getTotalNumberOfRepositories() {
        return totalNumberOfRepositories;
    }

    public List<RepositoriesPopularityScoreDTO> getRepositoriesPopularityScoreDTO() {
        return repositoriesPopularityScoreDTO;
    }

    public void setTotalNumberOfRepositories(int totalNumberOfRepositories) {
        this.totalNumberOfRepositories = totalNumberOfRepositories;
    }

    public void setRepositoriesPopularityScoreDTO(List<RepositoriesPopularityScoreDTO> repositoriesPopularityScoreDTO) {
        this.repositoriesPopularityScoreDTO = repositoriesPopularityScoreDTO;
    }
}
