Feature: Presence
  Background:
    Given I have a valid publish key
    And I have a valid subscribe key
    And I create PubNub instance for PeerA
    And I create PubNub instance for PeerB
    And I have a random channel (test channel)

  Scenario: Peers can observe presence
    Given PeerA registers listener for presence changes
    And PeerA subscribes to test channel (including presence changes)
    And PeerA's subscription is up and running
    When PeerB subscribes to test channel
    Then PeerA in notified that PeerB has joined test channel
