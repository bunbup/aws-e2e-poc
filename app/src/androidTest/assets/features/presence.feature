Feature: Presence
  Background:
    Given I have a valid publish key
    And I have a valid subscribe key
    And I create PubNub instance for PeerA
    And I create PubNub instance for PeerB
    And I have a random channel test

  Scenario: Peers can observe presence
    Given PeerA registers listener for presence changes
    And PeerA subscribes to channel test including presence changes
    And PeerA's subscription is up and running
    When PeerB subscribes to channel test
    Then PeerA is notified that PeerB has joined channel test
