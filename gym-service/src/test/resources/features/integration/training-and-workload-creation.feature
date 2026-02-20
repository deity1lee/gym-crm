Feature: Training creation updates trainer workload
  Background:
    Given a trainee exists with first name "Anna", last name "Klein" and address "Brooklyn 1"
    And a trainer exists with first name "Emma", last name "Smith"
    And user is logged in
    And the "Emma.Smith" trainer's workload on 5 month of 2026 year is retrieved

  Scenario: Creating a training updates trainer workload
    When the user creates a training for trainee "Anna.Klein" and trainer "Emma.Smith" on "2026-05-13"
    Then training should "" be added
    And the "Emma.Smith" trainer's workload on 5 month of 2026 year should "" be increased by 6 hours
    And the response status should be 201

  Scenario: Creating a training does not update trainer workload because of gym service validation error, rollback occurs
    When the user creates a training for trainee "Not.Existing" and trainer "Emma.Smith" on "2026-05-13"
    Then training should "not" be added
    And the "Emma.Smith" trainer's workload on 5 month of 2026 year should "not" be increased by 0 hours
    And the response status should be 400

  Scenario: Creating a training does not update trainer workload because of trainer workload service validation error, rollback occurs
    When the user creates a training for trainee "Anna.Klein" and trainer "Emma.Smith" on "2023-05-13"
    Then the "Emma.Smith" trainer's workload on 5 month of 2026 year should "not" be increased by 0 hours
    And training should "not" be added
    And the response status should be 201
