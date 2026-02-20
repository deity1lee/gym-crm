Feature: Trainer creation

  Background:
    Given a trainee exists with first name "Sara", last name "Gilbert" and address "Brooklyn 1"
    And user is logged in

  Scenario: Successful trainer creation
    When the user tries to create a trainer with first name "Anna", last name "Klein"
    Then the trainer "Anna.Klein" should "" be persisted
    And the response status should be 201

  Scenario: Unsuccessful trainer creation
    When the user tries to create a trainer with first name "", last name "Klein"
    Then the trainer "Anna.Klein" should "not" be persisted
    And a validation exception should be thrown with the message: "Validation failed: firstName First name is required"
    And the response status should be 400
