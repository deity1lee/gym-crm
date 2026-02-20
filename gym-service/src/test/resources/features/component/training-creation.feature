Feature: Training creation
  Background:
    Given a trainee exists with first name "Anna", last name "Klein" and address "Brooklyn 1"
    And a trainer exists with first name "Emma", last name "Smith"
    And user is logged in

  Scenario: Creating a training interacts with trainer workload service
    When the user creates a training for trainee "Anna.Klein" and trainer "Emma.Smith" on "2026-05-13"
    Then training should "" be added
    Then the workload service should be notified for 1 trainings
    And the response status should be 201

  Scenario: Creating a training does not update trainer workload because of gym service validation error, rollback occurs
    When the user creates a training for trainee "Not.Existing" and trainer "Emma.Smith" on "2026-05-13"
    Then training should "not" be added
    Then the workload service should be notified for 0 trainings
    And the response status should be 400
