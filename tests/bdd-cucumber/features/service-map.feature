Feature: API gateway service discovery
  Scenario: Gateway exposes its configured downstream services
    When I request the service map from the API gateway
    Then the response status should be 200
    And the gateway response should include the flight service URL
    And the gateway response should include the simulator service URL
