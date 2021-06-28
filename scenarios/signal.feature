Feature: Signals
  Background:
    Given I have a valid publish key
    And I have a valid subscribe key
    And I create PubNub instance for PeerA
    And I create PubNub instance for PeerB
    Given I have a random channel (test channel)

  Scenario: Peers can observe presence
    Given PeerA registers listener for signals
    And PeerA subscribes to test channel
    And PeerA's subscription is up and running
    And Random signal (test signal) is generated
    When PeerA emits test signal
    Then PeerB in notified that signal has been received
    And Received signal is same as test signal
