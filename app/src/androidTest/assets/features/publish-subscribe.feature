Feature: Publish and Subscribe
  Background:
    Given I have a valid publish key
    And I have a valid subscribe key
    And I create PubNub instance for PeerA
    And I create PubNub instance for PeerB
    And I have a random channel test channel

  Scenario: Peers can exchange messages over channel
    Given PeerB registers listener for incoming messages
    And PeerB subscribes to test channel
    And PeerB's subscription is up and running
    And Random message test message is generated
    When PeerA publishes test message to test channel
    Then PeerB in notified that message has arrived
    And Received message is same as test message
