package com.popularity.scoring.service;

import com.popularity.scoring.model.GithubPopularityScoreResponse;
import com.popularity.scoring.model.RepositoriesPopularityScoreDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.assertThat;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;

@ExtendWith(MockitoExtension.class)
public class ScoringUtilTest {
    @InjectMocks
    private ScoringUtil scoringUtil;

    @Mock
    private ScoreCalculatorService scoreCalculatorService;

    private RepositoriesPopularityScoreDTO repository1;
    private RepositoriesPopularityScoreDTO repository2;
    private RepositoriesPopularityScoreDTO repository3;

    @BeforeEach
    void setup() {
        // Mock repositories
        repository1 = new RepositoriesPopularityScoreDTO("repo1", 100, 50, LocalDateTime.now().minusDays(5), LocalDateTime.now().minusDays(30), 0);
        repository2 = new RepositoriesPopularityScoreDTO("repo2", 100, 50, LocalDateTime.now().minusDays(5), LocalDateTime.now().minusDays(30), 0);
        repository3 = new RepositoriesPopularityScoreDTO("repo3", 100, 50, null, LocalDateTime.now().minusDays(30), 0);

        scoreCalculatorService = new ScoreCalculatorService(null, null);
    }

    @Test
    void shouldCalculateScoresForValidRepositories() {
        // Arrange
        GithubPopularityScoreResponse response = new GithubPopularityScoreResponse(3, Arrays.asList(repository1, repository2, repository3));

        // Act
        GithubPopularityScoreResponse result = scoringUtil.calculatePopularityScores(response);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getRepositoriesPopularityScoreDTO()).hasSize(3);
        assertThat(repository1.getPopularityScore()).isEqualTo(154.77650730540356);
        assertThat(repository2.getPopularityScore()).isEqualTo(154.77650730540356);
        assertThat(repository3.getPopularityScore()).isEqualTo(0.0); // Expect 0 due to missing lastUpdated
    }

    @Test
    void shouldReturnEmptyResponseForEmptyRepositoryList() {
        // Arrange
        GithubPopularityScoreResponse response = new GithubPopularityScoreResponse(0, Collections.emptyList());

        // Act
        GithubPopularityScoreResponse result = scoringUtil.calculatePopularityScores(response);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getRepositoriesPopularityScoreDTO()).isEmpty();
    }

    @Test
    void shouldHandleNullRepositoryList() {
        // Arrange
        GithubPopularityScoreResponse response = new GithubPopularityScoreResponse(0, null);

        // Act
        GithubPopularityScoreResponse result = scoringUtil.calculatePopularityScores(response);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getRepositoriesPopularityScoreDTO()).isEmpty();
    }

    @Test
    void shouldAssignMinScoreForRepositoriesWithoutLastUpdatedDate() {
        // Arrange
        repository3.setLastUpdated(null); // Set lastUpdated to null
        GithubPopularityScoreResponse response = new GithubPopularityScoreResponse(1, Collections.singletonList(repository3));

        // Act
        GithubPopularityScoreResponse result = scoringUtil.calculatePopularityScores(response);

        // Assert
        assertThat(result).isNotNull();
        assertThat(repository3.getPopularityScore()).isEqualTo(0.0); // Minimum score expected
    }

}
