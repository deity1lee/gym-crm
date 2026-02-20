Feature: Trainer workload request
  Background:
    Given a trainee exists with first name "Anna", last name "Klein" and address "Brooklyn 1"
    And a trainer exists with first name "Emma", last name "Smith"
    And user is logged in
    And the user creates a training for trainee "Anna.Klein" and trainer "Emma.Smith" on "2026-05-13"

  Scenario: Retrieving trainer workload which does not exist
    When the "Emily.Smith" trainer's workload on 5 month of 2026 year is retrieved
    Then an exception should be thrown, stating that trainer "Emily.Smith" is not found
    And the response status should be 400

  Scenario: Retrieving trainer workload which exists
    When the "Emma.Smith" trainer's workload on 5 month of 2026 year is retrieved
    And the trainer's workload should be returned
    And the response status should be 200
