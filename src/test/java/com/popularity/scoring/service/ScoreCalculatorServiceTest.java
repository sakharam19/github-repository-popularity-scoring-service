package com.popularity.scoring.service;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

import com.popularity.scoring.client.GitHubClient;
import com.popularity.scoring.exceptionhandling.RepositoryServiceException;
import com.popularity.scoring.model.GithubPopularityScoreResponse;
import com.popularity.scoring.model.RepositoriesPopularityScoreDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;

@ExtendWith(MockitoExtension.class)
class ScoreCalculatorServiceTest {

    @Mock
    private GitHubClient gitHubClient;

    @Mock
    private ScoringUtil scoringUtil;

    @InjectMocks
    private ScoreCalculatorService scoreCalculatorService;

    private static final String TEST_LANGUAGE = "Java";
    private static final LocalDate TEST_DATE = LocalDate.of(2024, 1, 1);
    private static final int TEST_PAGE = 1;

    @BeforeEach
    void setup() {
        scoreCalculatorService = new ScoreCalculatorService(gitHubClient, scoringUtil);
    }

    @Test
    void shouldFetchAndScoreRepositories_whenRepositoriesAreFetchedSuccessfully() {

        GithubPopularityScoreResponse mockResponse = new GithubPopularityScoreResponse(1, Collections.singletonList(new RepositoriesPopularityScoreDTO("repo1", 100, 50, LocalDateTime.now().minusDays(5), LocalDateTime.now().minusDays(30), 0)));
        GithubPopularityScoreResponse scoredResponse = new GithubPopularityScoreResponse(1, Collections.singletonList(new RepositoriesPopularityScoreDTO("repo1", 100, 50, LocalDateTime.now().minusDays(5), LocalDateTime.now().minusDays(30), 300)));

        when(gitHubClient.searchAllRepositories(TEST_LANGUAGE, TEST_DATE, TEST_PAGE)).thenReturn(mockResponse);
        when(scoringUtil.calculatePopularityScores(mockResponse)).thenReturn(scoredResponse);

        GithubPopularityScoreResponse result = scoreCalculatorService.fetchAndScoreRepositories(TEST_LANGUAGE, TEST_DATE, TEST_PAGE);

        assertThat(result).isNotNull();
        assertThat(result.getRepositoriesPopularityScoreDTO()).isEqualTo(scoredResponse.getRepositoriesPopularityScoreDTO());
        verify(gitHubClient, times(1)).searchAllRepositories(TEST_LANGUAGE, TEST_DATE, TEST_PAGE);
        verify(scoringUtil, times(1)).calculatePopularityScores(mockResponse);
    }

    @Test
    void shouldReturnEmptyResponse_whenNoRepositoriesAreFound() {

        GithubPopularityScoreResponse mockResponse = new GithubPopularityScoreResponse(0, Collections.emptyList());

        when(gitHubClient.searchAllRepositories(TEST_LANGUAGE, TEST_DATE, TEST_PAGE)).thenReturn(mockResponse);

        GithubPopularityScoreResponse result = scoreCalculatorService.fetchAndScoreRepositories(TEST_LANGUAGE, TEST_DATE, TEST_PAGE);

        assertThat(result).isNotNull();
        assertThat(result.getRepositoriesPopularityScoreDTO()).isEmpty();
        verify(gitHubClient, times(1)).searchAllRepositories(TEST_LANGUAGE, TEST_DATE, TEST_PAGE);
        verify(scoringUtil, never()).calculatePopularityScores(any());
    }

    @Test
    void shouldThrowRepositoryServiceException_whenFetchingRepositoriesFails() {

        when(gitHubClient.searchAllRepositories(TEST_LANGUAGE, TEST_DATE, TEST_PAGE)).thenThrow(new RuntimeException("GitHub API failure"));

        assertThatExceptionOfType(RepositoryServiceException.class)
                .isThrownBy(() -> scoreCalculatorService.fetchAndScoreRepositories(TEST_LANGUAGE, TEST_DATE, TEST_PAGE))
                .withMessage("Failed to fetch repositories from GitHub");
        verify(gitHubClient, times(1)).searchAllRepositories(TEST_LANGUAGE, TEST_DATE, TEST_PAGE);
        verify(scoringUtil, never()).calculatePopularityScores(any());
    }

    @Test
    void shouldThrowRepositoryServiceException_whenCalculatingScoresFails() {

        GithubPopularityScoreResponse mockResponse = new GithubPopularityScoreResponse(1, Collections.singletonList(new RepositoriesPopularityScoreDTO()));

        when(gitHubClient.searchAllRepositories(TEST_LANGUAGE, TEST_DATE, TEST_PAGE)).thenReturn(mockResponse);
        when(scoringUtil.calculatePopularityScores(mockResponse)).thenThrow(new RuntimeException("Score calculation failure"));

        assertThatExceptionOfType(RepositoryServiceException.class)
                .isThrownBy(() -> scoreCalculatorService.fetchAndScoreRepositories(TEST_LANGUAGE, TEST_DATE, TEST_PAGE))
                .withMessage("Failed to calculate repository scores");
        verify(gitHubClient, times(1)).searchAllRepositories(TEST_LANGUAGE, TEST_DATE, TEST_PAGE);
        verify(scoringUtil, times(1)).calculatePopularityScores(mockResponse);
    }
}
