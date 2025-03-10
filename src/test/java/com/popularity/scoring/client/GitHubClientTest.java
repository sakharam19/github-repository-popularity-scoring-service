package com.popularity.scoring.client;

import com.popularity.scoring.exceptionhandling.RepositoryServiceException;
import com.popularity.scoring.model.GitHubApiResponse;
import com.popularity.scoring.model.GithubPopularityScoreResponse;
import com.popularity.scoring.model.RepositoriesPopularityScoreDTO;
import com.popularity.scoring.model.RepositoryItems;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GitHubClientTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private GitHubClient gitHubClient;

    private final String githubApiBaseUrl = "https://api.github.com";

    @BeforeEach
    void setUp() throws Exception {
        // Use reflection to set the private field githubApiBaseUrl
        Field field = GitHubClient.class.getDeclaredField("githubApiBaseUrl");
        field.setAccessible(true); // Allow access to private field
        field.set(gitHubClient, githubApiBaseUrl); // Set the value
    }

    @Test
    void testSearchAllRepositories_Success() {
        String language = "java";
        LocalDate earliestDate = LocalDate.of(2023, 1, 1);
        int page = 1;

        RepositoryItems repositoryItem = new RepositoryItems();
        repositoryItem.setFullName("test/repo");
        repositoryItem.setStars(100);
        repositoryItem.setForks(50);
        repositoryItem.setLastUpdated(LocalDateTime.now());
        repositoryItem.setCreatedDate(LocalDateTime.now());
        repositoryItem.setRepoURL("https://github.com/test/repo");

        GitHubApiResponse gitHubApiResponse = new GitHubApiResponse();
        gitHubApiResponse.setTotalCount(1);
        gitHubApiResponse.setItems(Collections.singletonList(repositoryItem));

        ResponseEntity<GitHubApiResponse> responseEntity = new ResponseEntity<>(gitHubApiResponse, HttpStatus.OK);

        when(restTemplate.getForEntity(anyString(), eq(GitHubApiResponse.class))).thenReturn(responseEntity);

        GithubPopularityScoreResponse result = gitHubClient.searchAllRepositories(language, earliestDate, page);

        assertThat(result.getTotalNumberOfRepositories()).isEqualTo(1);
        assertThat(result.getRepositoriesPopularityScoreDTO()).hasSize(1);

        RepositoriesPopularityScoreDTO dto = result.getRepositoriesPopularityScoreDTO().get(0);
        assertThat(dto.getFullName()).isEqualTo("test/repo");
        assertThat(dto.getStars()).isEqualTo(100);
        assertThat(dto.getForks()).isEqualTo(50);
    }

    @Test
    void testSearchAllRepositories_NoRepositoriesFound() {

        String language = "java";
        LocalDate earliestDate = LocalDate.of(2023, 1, 1);
        int page = 1;

        GitHubApiResponse gitHubApiResponse = new GitHubApiResponse();
        gitHubApiResponse.setTotalCount(0);
        gitHubApiResponse.setItems(Collections.emptyList());

        ResponseEntity<GitHubApiResponse> responseEntity = new ResponseEntity<>(gitHubApiResponse, HttpStatus.OK);

        when(restTemplate.getForEntity(anyString(), eq(GitHubApiResponse.class))).thenReturn(responseEntity);

        GithubPopularityScoreResponse result = gitHubClient.searchAllRepositories(language, earliestDate, page);

        assertThat(result.getRepositoriesPopularityScoreDTO()).isEqualTo(Collections.emptyList());
        assertThat(result.getRepositoriesPopularityScoreDTO()).isEmpty();
    }

    @Test
    void testSearchAllRepositories_ApiFailure() {
        String language = "java";
        LocalDate earliestDate = LocalDate.of(2023, 1, 1);
        int page = 1;

        when(restTemplate.getForEntity(anyString(), eq(GitHubApiResponse.class)))
                .thenThrow(new RestClientException("API call failed"));

        assertThatThrownBy(() -> gitHubClient.searchAllRepositories(language, earliestDate, page))
                .isInstanceOf(RepositoryServiceException.class)
                .hasMessageContaining("Error fetching repositories from GitHub");
    }

    @Test
    void testSearchAllRepositories_Non2xxResponse() {
        String language = "java";
        LocalDate earliestDate = LocalDate.of(2023, 1, 1);
        int page = 1;

        ResponseEntity<GitHubApiResponse> responseEntity = new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        when(restTemplate.getForEntity(anyString(), eq(GitHubApiResponse.class))).thenReturn(responseEntity);

        assertThatThrownBy(() -> gitHubClient.searchAllRepositories(language, earliestDate, page))
                .isInstanceOf(RepositoryServiceException.class)
                .hasMessageContaining("Failed to fetch repositories. Status: 400 BAD_REQUEST");
    }
}