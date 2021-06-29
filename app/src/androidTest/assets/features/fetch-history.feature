Feature: Fetch History
  Background:
    Given I create PubNub instance for PeerA
    And I create PubNub instance for PeerB
    And I have a random channel

  Scenario: Peers can exchange messages over channel
    Given Random message content is generated
    And PeerA publishes test message to test channel with store flag set
    And Start time taken from Timetoken is generated
    When PeerB fetches the history of test channel including range from start time till present moment
    Then The list of past messages includes test message for test channel
