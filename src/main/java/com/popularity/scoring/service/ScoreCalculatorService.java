package com.popularity.scoring.service;

import com.popularity.scoring.client.GitHubClient;
import com.popularity.scoring.exceptionhandling.RepositoryServiceException;
import com.popularity.scoring.model.GithubPopularityScoreResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class ScoreCalculatorService {

    private final ScoringUtil scoringUtil;

    private final GitHubClient gitHubClient;

    private static final Logger logger = LoggerFactory.getLogger(ScoreCalculatorService.class);

    public ScoreCalculatorService(GitHubClient gitHubClient, ScoringUtil scoringUtil) {
        this.gitHubClient = gitHubClient;
        this.scoringUtil = scoringUtil;
    }

    /**
     * Fetches repositories from GitHub and calculates their popularity scores.
     *
     * @param language     The programming language filter.
     * @param earliestDate The earliest creation date filter.
     * @param pageNumber   The page number for pagination.
     * @return A response containing the scored repositories.
     */
    public GithubPopularityScoreResponse fetchAndScoreRepositories(final String language, final LocalDate earliestDate, final int pageNumber) {
        logger.info("Fetching repositories for language: {}, earliestDate: {}, pageNumber: {}", language, earliestDate, pageNumber);

        GithubPopularityScoreResponse response = fetchRepositories(language, earliestDate, pageNumber);

        if (response.getRepositoriesPopularityScoreDTO().isEmpty()) {
            logger.warn("No repositories found for language: {} with earliestDate: {}", language, earliestDate);
            return response;
        }

        return calculateScores(response);
    }

    /**
     * Fetches repositories from GitHub based on language and date filters.
     *
     * @param language     The programming language filter.
     * @param earliestDate The earliest creation date filter.
     * @param pageNumber   The page number for pagination.
     * @return A response containing repositories.
     */
    private GithubPopularityScoreResponse fetchRepositories(final String language, final LocalDate earliestDate, final int pageNumber) {
        try {
            return gitHubClient.searchAllRepositories(language, earliestDate, pageNumber);
        } catch (Exception e) {
            logger.error("Error fetching repositories from GitHub for language: {}, earliestDate: {}, pageNumber: {}", language, earliestDate, pageNumber, e);
            throw new RepositoryServiceException("Failed to fetch repositories from GitHub", e);
        }
    }

    /**
     * Calculates popularity scores for the fetched repositories.
     *
     * @param response The initial response containing repositories.
     * @return A response with calculated scores.
     */
    private GithubPopularityScoreResponse calculateScores(final GithubPopularityScoreResponse response) {
        try {
            GithubPopularityScoreResponse scoredResponse = scoringUtil.calculatePopularityScores(response);
            logger.info("Successfully calculated scores for {} repositories", scoredResponse.getRepositoriesPopularityScoreDTO().size());
            return scoredResponse;
        } catch (Exception e) {
            logger.error("Error calculating repository scores", e);
            throw new RepositoryServiceException("Failed to calculate repository scores", e);
        }
    }
}
