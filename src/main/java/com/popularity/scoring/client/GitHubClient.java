package com.popularity.scoring.client;

import com.popularity.scoring.exceptionhandling.RepositoryServiceException;
import com.popularity.scoring.model.GitHubApiResponse;
import com.popularity.scoring.model.GithubPopularityScoreResponse;
import com.popularity.scoring.model.RepositoriesPopularityScoreDTO;
import com.popularity.scoring.model.RepositoryItems;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

@Component
public class GitHubClient {

    private static final Logger logger = LoggerFactory.getLogger(GitHubClient.class);

    private final RestTemplate restTemplate;

    @Value("${github.api.base-url}")
    private String githubApiBaseUrl;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");


    @Autowired
    public GitHubClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Fetches repositories from GitHub based on language and creation date, ordered by popularity.
     *
     * @param language      Programming language filter.
     * @param earliestDate  Earliest repository creation date.
     * @param page          Page number for pagination.
     * @return Response containing repository popularity scores.
     */
    public GithubPopularityScoreResponse searchAllRepositories(String language, LocalDate earliestDate, int page) {
        String formattedDate = earliestDate.format(DATE_FORMATTER);

        logger.info("Fetching repositories - Language: {}, Created After: {}, Page: {}", language, formattedDate, page);

        String url = buildGitHubApiUrl(language, formattedDate, page);

        try {
            ResponseEntity<GitHubApiResponse> response = restTemplate.getForEntity(url, GitHubApiResponse.class);

            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                logger.error("GitHub API request failed - Status: {}, URL: {}", response.getStatusCode(), url);
                throw new RepositoryServiceException("Failed to fetch repositories. Status: " + response.getStatusCode());
            }

            GitHubApiResponse responseBody = response.getBody();
            if (responseBody.getTotalCount() == 0) {
                logger.info("No repositories found for Language: {} after {}", language, formattedDate);
                return new GithubPopularityScoreResponse(0, Collections.emptyList());
            }

            List<RepositoriesPopularityScoreDTO> repositories = responseBody.getItems().stream()
                    .map(this::mapToDto)
                    .toList();

            logger.info("Fetched {} repositories from GitHub for page {}", repositories.size(), page);
            return new GithubPopularityScoreResponse(responseBody.getTotalCount(), repositories);

        } catch (RestClientException e) {
            logger.error("Error calling GitHub API: {}", e.getMessage(), e);
            throw new RepositoryServiceException("Error fetching repositories from GitHub", e);
        }
    }

    /**
     * Constructs the GitHub API URL dynamically.
     */
    private String buildGitHubApiUrl(String language, String formattedDate, int page) {
        StringBuilder url = new StringBuilder(githubApiBaseUrl)
                .append("/search/repositories?q=created:>")
                .append(formattedDate)
                .append("+language:")
                .append(language)
                .append("&sort=stars&order=desc&per_page=100&page=")
                .append(page);

        return url.toString();
    }

    /**
     * Maps GitHub API response items to DTOs.
     */
    private RepositoriesPopularityScoreDTO mapToDto(RepositoryItems repository) {
        return new RepositoriesPopularityScoreDTO.Builder()
                .fullName(repository.getFullName())
                .stars(repository.getStars())
                .forks(repository.getForks())
                .lastUpdated(repository.getLastUpdated())
                .createdDate(repository.getCreatedDate())
                .repositoryURL(repository.getRepoURL())
                .build();
    }
}