Feature: Trainee creation

  Background:
    Given a trainer exists with first name "Emma", last name "Smith"
    And user is logged in

  Scenario: Successful trainee creation
    When the user tries to create a trainee with first name "Anna", last name "Klein" and address "Brooklyn 1"
    Then the trainee "Anna.Klein" should "" be persisted
    And the response status should be 201

  Scenario: Unsuccessful trainee creation
    When the user tries to create a trainee with first name "", last name "Klein" and address "Brooklyn 1"
    Then the trainee "Anna.Klein" should "not" be persisted
    And a validation exception should be thrown with the message: "Validation failed: firstName First name is required"
    And the response status should be 400

