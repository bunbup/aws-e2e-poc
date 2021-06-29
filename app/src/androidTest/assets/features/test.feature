Feature: Test
  Background:
    Given __I create PubNub instance for PeerA
    And __I create PubNub instance for PeerB

  Scenario: Test
    Given __PeerA registers listener for signals
    And __PeerA subscribes to test channel
    When __PeerB emits test signal
    Then __PeerA received test signal

