Feature: Presence
  Background:
    Given I create PubNub instance for PeerA
    And I create PubNub instance for PeerB
    And I have a random channel

  Scenario: Peers can observe presence
    Given PeerA registers listener for presence
    And PeerA subscribes to test channel including presence changes
    And PeerA's subscription is up and running
    When PeerB subscribes to test channel
    Then PeerA is notified that PeerB has joined test channel
