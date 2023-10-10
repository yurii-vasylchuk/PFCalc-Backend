@api
@disabled
  Feature: Authentication

    Scenario: Successfully register with correct username/password
      When I make a GET call on /status
      Then I should receive 200 response status code