@workload
Feature: Get Trainer workload for specified year & month

  Scenario: Trainer has some working hours in that month
    Given Trainer "John.Do" with 60 minutes workload on year 2026 and month 4
    When Trainer workload is requested for username "John.Do", year 2026, and month 4
    Then 60 should be returned

  Scenario: Trainer dose not have any working hours in that month
    Given Trainer "Doe.Do" with 0 minutes workload on year 2026 and month 4
    When Trainer workload is requested for username "Doe.Do", year 2026, and month 4
    Then 0 should be returned

  Scenario: Trainer dose not have any working hours in that month
    When Trainer workload is requested for username "Doe.Doe", year 2026, and month 4
    Then 0 should be returned

  Scenario: Trainer has some working hours in that month but request is not authorized
    Given Trainer "John.Do" with 60 minutes workload on year 2026 and month 4
    When Trainer workload is requested for username "John.Do", year 2026, and month 4 but is not authorized
    Then 401 Unauthorized should be returned