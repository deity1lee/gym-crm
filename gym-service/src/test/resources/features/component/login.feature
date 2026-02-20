Feature: User Login

  Scenario: Successful login with valid credentials
    Given a trainee exists with first name "Sara", last name "Gilbert" and address "Brooklyn 1"
    When the user logs in with provided credentials
    Then the login response status should be 200
    And the response should contain a JWT token

  Scenario: Unsuccessful login with wrong credentials
    Given a user that does not exist
    When the user logs in with username "some_username" and password "some_password"
    Then the login response status should be 400
    And the response should not contain a JWT token