package com.pubnub.androidapp

import com.pubnub.androidapp.state.Peers
import com.pubnub.androidapp.state.TestChannel
import io.cucumber.java.en.And

class SubscribeStepDefinition(private val peers: Peers, private val testChannel: TestChannel) {

    @And("{peerId} subscribes to test channel")
    fun subscribeToTestChannel(peerId: String) {
        val peer = peers.peers[peerId]
        peer?.pubnub?.subscribe(channels = listOf(testChannel.testChannel))
    }

    @And("{peerId} subscribes to test channel including presence changes")
    fun subscribeToTestChannelIncludingPresence(peerId: String) {
        val peer = peers.peers[peerId]
        peer?.pubnub?.subscribe(channels = listOf(testChannel.testChannel), withPresence = true)
    }
}