Feature: Trainee deletion

  Background:
    Given a trainee exists with first name "Sara", last name "Gilbert" and address "Brooklyn 1"
    And user is logged in

  Scenario: Successful trainee deletion
    Given a trainee "jo" "doe" with trainings on "2026-04-15" and "2026-04-17"
    When I delete the trainee "jo.doe"
    Then the workload service should be notified for 2 trainings
    And the trainee "jo.doe" with its trainings should no longer exist
    And the response status should be 200

  Scenario: Unsuccessful trainee deletion when trainee does not exist
    When I delete the trainee "jo.doe"
    Then an exception should be thrown, stating that trainee "jo.doe" is not found
    And the workload service should be notified for 0 trainings
    And the response status should be 400
