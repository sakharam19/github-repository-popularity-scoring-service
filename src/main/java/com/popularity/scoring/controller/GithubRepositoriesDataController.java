package com.popularity.scoring.controller;

import com.popularity.scoring.exceptionhandling.PageLimitExceededException;
import com.popularity.scoring.model.GithubPopularityScoreResponse;
import com.popularity.scoring.service.ScoreCalculatorService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1")
public class GithubRepositoriesDataController {

    private final ScoreCalculatorService scoreCalculatorService;

    private static final Logger logger = LoggerFactory.getLogger(GithubRepositoriesDataController.class);
    private static final int MAX_PAGE_LIMIT = 10;

    public GithubRepositoriesDataController(ScoreCalculatorService scoreCalculatorService) {
        this.scoreCalculatorService = scoreCalculatorService;
    }

    /**
     * Fetches popular GitHub repositories based on query parameters and calculates popularity scores.
     *
     * @param language     The programming language filter (required).
     * @param earliestDate The earliest creation date filter (required).
     * @param pageNumber   The page number for pagination (optional, default = 1, max = 10).
     * @return A ResponseEntity containing the list of repositories with popularity scores.
     */
    @GetMapping("/calculateGithubRepositoryPopularityScore")
    public ResponseEntity<GithubPopularityScoreResponse> calculateGithubRepositoryScore(
            @RequestParam @NotBlank(message = "Language parameter is required") String language,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            @PastOrPresent(message = "Earliest date must be in the past or present") LocalDate earliestDate,
            @RequestParam(defaultValue = "1") int pageNumber) {

        validatePageNumber(pageNumber);

        GithubPopularityScoreResponse response = scoreCalculatorService.fetchAndScoreRepositories(language, earliestDate, pageNumber);

        return ResponseEntity.ok(response);
    }

    /**
     * Validates that the page number does not exceed the allowed limit.
     *
     * @param pageNumber The page number provided by the user.
     */
    private void validatePageNumber(int pageNumber) {
        if (pageNumber < 1 || pageNumber > MAX_PAGE_LIMIT) {
            logger.error("Invalid page number: {}. Must be between 1 and {}", pageNumber, MAX_PAGE_LIMIT);
            throw new PageLimitExceededException("Page number must be between 1 and " + MAX_PAGE_LIMIT);
        }
    }

}
