package com.popularity.scoring.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;

import com.popularity.scoring.model.GithubPopularityScoreResponse;
import com.popularity.scoring.service.ScoreCalculatorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
public class GithubRepositoriesDataControllerTest {

    @Mock
    private ScoreCalculatorService scoreCalculatorService;

    @InjectMocks
    private GithubRepositoriesDataController githubRepositoriesDataController;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(githubRepositoriesDataController).build();
    }

    @Test
    public void testCalculateGithubRepositoryScore_ValidRequest_Returns200() throws Exception {
        String language = "Java";
        LocalDate earliestDate = LocalDate.now().minusDays(10);
        int pageNumber = 1;

        Mockito.when(scoreCalculatorService.fetchAndScoreRepositories(language, earliestDate, pageNumber))
                .thenReturn(new GithubPopularityScoreResponse());

        mockMvc.perform(get("/api/v1/calculateGithubRepositoryPopularityScore")
                        .param("language", language)
                        .param("earliestDate", earliestDate.toString())
                        .param("pageNumber", String.valueOf(pageNumber))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void testCalculateGithubRepositoryScore_MissingLanguage_Returns400() throws Exception {
        LocalDate earliestDate = LocalDate.now().minusDays(10);
        int pageNumber = 1;

        mockMvc.perform(get("/api/v1/calculateGithubRepositoryPopularityScore")
                        .param("earliestDate", earliestDate.toString())
                        .param("pageNumber", String.valueOf(pageNumber))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testCalculateGithubRepositoryScore_InvalidEarliestDate_Returns400() throws Exception {
        String language = "Java";
        LocalDate earliestDate = LocalDate.now().plusDays(10); // Future date
        int pageNumber = 1;

        mockMvc.perform(get("/api/v1/calculateGithubRepositoryPopularityScore")
                        .param("language", language)
                        .param("earliestDate", earliestDate.toString())
                        .param("pageNumber", String.valueOf(pageNumber))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testCalculateGithubRepositoryScore_MissingEarliestDate_Returns400() throws Exception {
        String language = "Java";
        int pageNumber = 1;

        mockMvc.perform(get("/api/v1/calculateGithubRepositoryPopularityScore")
                        .param("language", language)
                        .param("pageNumber", String.valueOf(pageNumber))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}