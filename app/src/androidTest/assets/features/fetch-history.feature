Feature: Fetch History
  Background:
    Given I have a valid publish key
    And I have a valid subscribe key
    And I create PubNub instance for PeerA
    And I create PubNub instance for PeerB
    And _I have a random test channel

  Scenario: Peers can exchange messages over channel
    Given Random test message is generated
    And PeerA publishes test message to test channel with store flag set
    And Timetoken of published message is stored
    And Start time taken from Timetoken is generated
    When PeerB fetches the history of test channel including range from start time till present moment
    Then PeerB retrieves list of past messages
    And The list of past messages includes test message
