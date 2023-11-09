@api
Feature: Authentication

  Scenario: Register with correct username/password
    Given prepared request with following data:
      """json
      {
        "email": "alpha@test.com",
        "password": "password",
        "name": "alpha",
        "preferredLanguage": "UA"
      }
      """
    When I'm sending POST request to "/api/user/register"
    Then I should receive successful response
    And User "alpha@test.com" has been saved in db
    And Verify email token for "alpha@test.com" has been saved in db
    And response contain proper jwt token for user 'alpha'

  Scenario: Sign in with correct email and password
    Given User 'alpha' is present
    And prepared request with following data:
      """json
      {
        "email": "alpha@test.com",
        "password": "password"
      }
      """
    When I'm sending POST request to "/api/user/login"
    Then I should receive successful response
    And response contain proper jwt token for user 'alpha'

  Scenario: Verify email address
    Given User 'alpha' with unconfirmed email is present
    And verify email token is generated for user 'alpha'
    And prepared request with that verification token
    When I'm sending POST request to "/api/user/verify"
    Then I should receive successful response
    And response contain proper jwt token for user 'alpha'
    And email of user 'alpha' become confirmed

  Scenario: Complete profile
    Given User 'alpha' with uncompleted profile is present
    And I'm authenticated as 'alpha'
    And prepared request with following data:
      """json
      {
        "aims": {
          "protein": 100,
          "fat": 50,
          "carbohydrates": 200,
          "calories": 2000
        }
      }
      """
    When I'm sending POST request to "/api/user/profile"
    Then I should receive successful response
    And user 'alpha' should have completed profile with following aims
      | protein       | 100  |
      | fat           | 50   |
      | carbohydrates | 200  |
      | calories      | 2000 |

  # TODO: Implement GET Profile scenario
