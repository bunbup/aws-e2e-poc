package com.pubnub.androidapp

import io.cucumber.java.en.When
import java.util.*

class EmitEventStepDefinition(
    val peers: Peers,
    val testChannel: TestChannel,
    val testMessages: TestMessages
) {

    @When("{peerId} emits test {eventType}")
    fun emitSignal(peerId: String, eventType: String) {
        val peer = peers.peers[peerId]
        val event = "${eventType}-${UUID.randomUUID()}"
        testMessages.messages[eventType] = event
        when (eventType) {
            "signal" -> peer?.pubnub?.signal(channel = testChannel.testChannel, message = event)?.sync()
        }
    }
}