Feature: Publish and Subscribe
  Background:
    Given I create PubNub instance for PeerA
    And I create PubNub instance for PeerB
    And I have a random channel

  Scenario: Peers can exchange messages over channel
    Given PeerB registers listener for message
    And PeerB subscribes to test channel
    And PeerB's subscription is up and running
    And Random message content is generated
    When PeerA emits test message
    Then PeerB received message the same as generated
