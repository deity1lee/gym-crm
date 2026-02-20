Feature: Training deletion updates trainer workload
  Background:
    Given a trainee exists with first name "Anna", last name "Klein" and address "Brooklyn 1"
    And a trainer exists with first name "Emma", last name "Smith"
    And user is logged in
    And the user creates a training for trainee "Anna.Klein" and trainer "Emma.Smith" on "2026-05-13"
    And the "Emma.Smith" trainer's workload on 5 month of 2026 year is retrieved

  Scenario: Deleting a training updates trainer workload
    When the user deletes the training
    Then training should "" be deleted
    And the "Emma.Smith" trainer's workload on 5 month of 2026 year should "" be decreased by 6 hours
    And the response status should be 204

  Scenario: Deleting a training does not update trainer workload because one does not exist in trainer workload service storage,
    but training can still be deleted
    Given the user creates a training for trainee "Anna.Klein" and trainer "Emma.Smith" on "2026-06-15", but workload is not updated
    And the trainer's workload should be 0
    When the user deletes the training
    Then training should "" be deleted
    And the trainer's workload should be 0
    And the response status should be 204