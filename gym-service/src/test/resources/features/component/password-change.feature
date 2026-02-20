#@change-password
Feature: User Change Password

  Background:
    Given a trainee exists with first name "Sara", last name "Gilbert" and address "Brooklyn 1"
    And user is logged in

  Scenario: Successful change of password
    When the user tries to change password to valid "Pass123456" password
    Then the response status should be 200
    And response message should be "Password changed successfully"
    And password should be changed to "Pass123456"

  Scenario: Unsuccessful attempt to change password, too weak password
    When the user tries to change password to valid "invalid" password
    Then the response status should be 400
    And a validation exception should be thrown with the message: ""
    And password should not be changed
