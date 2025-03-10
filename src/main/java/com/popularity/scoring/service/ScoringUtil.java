package com.popularity.scoring.service;

import com.popularity.scoring.exceptionhandling.RepositoryServiceException;
import com.popularity.scoring.model.GithubPopularityScoreResponse;
import com.popularity.scoring.model.RepositoriesPopularityScoreDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;

@Service
public class ScoringUtil {

    private static final Logger logger = LoggerFactory.getLogger(ScoringUtil.class);

    /**
     * Calculates the popularity scores for a list of repositories.
     *
     * @param response The response containing repositories to calculate scores for.
     * @return A response with updated popularity scores.
     * @throws RepositoryServiceException If an error occurs during score calculation.
     */
    public GithubPopularityScoreResponse calculatePopularityScores(final GithubPopularityScoreResponse response) {
        if (response == null || response.getRepositoriesPopularityScoreDTO() == null) {
            logger.warn("Received null or empty repository list for score calculation.");
            return new GithubPopularityScoreResponse(0, Collections.emptyList());
        }

        List<RepositoriesPopularityScoreDTO> repositories = response.getRepositoriesPopularityScoreDTO();
        logger.info("Calculating popularity scores for {} repositories", repositories.size());

        try {
            repositories.parallelStream().forEachOrdered(this::computeAndSetScore);
        } catch (RuntimeException e) {
            logger.error("Failed to calculate popularity scores", e);
            throw new RepositoryServiceException("An error occurred while calculating repository scores.", e);
        }

        return response;
    }

    /**
     * Computes and sets the popularity score for a repository.
     *
     * @param repository The repository to calculate the score for.
     */
    private void computeAndSetScore(RepositoriesPopularityScoreDTO repository) {
        if (repository == null) {
            logger.warn("Encountered a null repository while calculating scores.");
            return;
        }

        double score = calculateScore(repository);
        repository.setPopularityScore(score);
    }

    /**
     * Calculates the popularity score for a single repository.
     *
     * The score is based on:
     * - **Stars**: Logarithmic scaling to prioritize repositories with high star counts.
     * - **Forks**: Logarithmic scaling to prioritize repositories with high fork counts.
     * - **Recency**: Inverse exponential decay to prioritize recently updated repositories.
     *
     * @param repository The repository to calculate the score for.
     * @return The calculated popularity score.
     */
    private double calculateScore(final RepositoriesPopularityScoreDTO repository) {
        if (repository.getLastUpdated() == null) {
            logger.warn("Repository {} has no last updated date. Assigning minimum score.", repository.getFullName());
            return 0.0;
        }

        // Stars Score
        double starsScore = Math.log10(repository.getStars() + 1) * 50;

        // Forks Score
        double forksScore = Math.log10(repository.getForks() + 1) * 30;

        // Recency Score
        long daysSinceLastUpdate = ChronoUnit.DAYS.between(repository.getLastUpdated(), LocalDateTime.now());
        double recencyScore = (1.0 / (1.0 + daysSinceLastUpdate)) * 20;

        // Total Score
        return starsScore + forksScore + recencyScore;
    }
}
