Feature: Signals
  Background:
    Given I create PubNub instance for PeerA
    And I create PubNub instance for PeerB
    And I have a random channel

  Scenario: Peers can observe presence
    Given PeerA registers listener for signal
    And PeerA subscribes to test channel
    And PeerA's subscription is up and running
    And Random signal content is generated
    When PeerB emits test signal
    Then PeerA received signal the same as generated

