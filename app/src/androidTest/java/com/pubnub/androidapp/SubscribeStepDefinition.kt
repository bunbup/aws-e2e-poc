package com.pubnub.androidapp

import io.cucumber.java.en.And

class SubscribeStepDefinition(val peers: Peers, val testChannel: TestChannel) {

    @And("__{peerId} subscribes to test channel")
    fun SubscribeToTestChannel(peerId: String) {
        val peer = peers.peers[peerId]
        peer?.pubnub?.subscribe(channels = listOf(testChannel.testChannel))
    }
}