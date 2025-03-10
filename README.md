# Repository Scoring Microservice

This is a Spring Boot-based microservice that calculates the popularity score of GitHub repositories based on stars, forks, and recency of updates. The service fetches repository data from the GitHub API and assigns a score using a custom algorithm.

## Table of Contents

- [Features](#features)
- [Prerequisites](#prerequisites)
- [Building and Running the Application](#building-and-running-the-application)
- [Accessing the API](#accessing-the-api)

## Features

- Fetches repository data from the GitHub API.
- Calculates a popularity score based on:
    - **Stars**: Logarithmic scaling to prioritize repositories with high star counts.
    - **Forks**: Logarithmic scaling to prioritize repositories with high fork counts.
    - **Recency**: Inverse exponential decay to prioritize recently updated repositories.
- Supports pagination (100 repositories per page).
- Handles invalid query parameters and service errors gracefully.
- **Limitation**: Only the first 1000 search results are available due to a limit set by the GitHub API.
- **Rate Limitation**: GitHub API limits the number of requests per second. Unauthenticated requests are limited to 60 requests per hour, and authenticated requests are limited to 5,000 requests per hour.

## Scoring Approach

The popularity score for a repository is calculated based on the following factors:

- **Stars**: Logarithmic scaling is applied to prioritize repositories with high star counts. This means that as the number of stars increases, the score increases at a decreasing rate.
- **Forks**: Similar to stars, forks are also logarithmically scaled to prioritize repositories that have been forked more times.
- **Recency**: An inverse exponential decay function is applied to give higher scores to repositories that have been recently updated. This ensures that active repositories are prioritized over those that have not been updated in a long time.

## Prerequisites

Before running the application, ensure you have the following installed:

- Java 21 or later.
- Maven or Gradle (for building the project).

## Building and Running the Application

1. Clone the Repository:

```sh
  git clone https://github.com/sakharam19/github-repository-popularity-scoring-service.git
  cd github-repository-popularity-scoring-service
````
2. Build the Application:
```sh
  mvn clean install
````
3. Run the Application:

```sh
  mvn spring-boot:run
````

## Accessing the API
API Endpoint

GET /api/v1/calculateGithubRepositoryPopularityScore?language=java&earliestDate=2025-03-09&pageNumber=1

This endpoint fetches repositories and calculates their popularity scores.

Query Parameters:

 - language: The programming language filter.
 - earliestDate: The earliest creation date filter.
 - pageNumber (optional): API will fetch 100 records per page and only up to 10 pages. default value set to 1

SAMPLE URL
```url
  http://localhost:8080/api/v1/calculateGithubRepositoryPopularityScore?language=java&earliestDate=2025-03-09&pageNumber=1
````
Sample Response

```json
  {
  "totalNumberOfRepositories": 3699,
  "repositoriesPopularityScoreDTO": [
    {
      "fullName": "wxwhhh/Chypass_pro",
      "stars": 2,
      "forks": 0,
      "lastUpdated": "2025-03-10T09:51:56",
      "createdDate": "2025-03-10T08:42:32",
      "popularityScore": 43.85606273598312,
      "repositoryURL": "https://github.com/wxwhhh/Chypass_pro"
    },
    {
      "fullName": "RossmaryV/proyec-ventaslp2",
      "stars": 1,
      "forks": 0,
      "lastUpdated": "2025-03-10T01:36:20",
      "createdDate": "2025-03-10T01:15:22",
      "popularityScore": 35.05149978319906,
      "repositoryURL": "https://github.com/RossmaryV/proyec-ventaslp2"
    },
    {
      "fullName": "thanhnam140307/Jeu_CTF",
      "stars": 1,
      "forks": 0,
      "lastUpdated": "2025-03-10T02:52:01",
      "createdDate": "2025-03-10T01:51:28",
      "popularityScore": 35.05149978319906,
      "repositoryURL": "https://github.com/thanhnam140307/Jeu_CTF"
    }
  ]
}

````