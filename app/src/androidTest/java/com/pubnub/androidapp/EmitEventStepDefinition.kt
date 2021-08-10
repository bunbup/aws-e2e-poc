package com.pubnub.androidapp

import com.pubnub.androidapp.state.Peers
import com.pubnub.androidapp.state.TestChannel
import com.pubnub.androidapp.state.TestMessages
import io.cucumber.java.en.When
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers

class EmitEventStepDefinition(
    private val peers: Peers,
    private val testChannel: TestChannel,
    private val testMessages: TestMessages
) {

    @When("{peerId} emits test {eventType}")
    fun emitEvent(peerId: String, eventType: String) {
        val peer = peers.peers[peerId]
        assertThat(testMessages.messages[eventType], Matchers.not(Matchers.isEmptyOrNullString()))
        when (eventType) {
            "signal" -> peer?.pubnub?.signal(
                channel = testChannel.testChannel,
                message = testMessages.messages[eventType]!!
            )?.sync()
            "message" -> peer?.pubnub?.publish(
                channel = testChannel.testChannel,
                message = testMessages.messages[eventType]!!
            )?.sync()
        }
    }
}