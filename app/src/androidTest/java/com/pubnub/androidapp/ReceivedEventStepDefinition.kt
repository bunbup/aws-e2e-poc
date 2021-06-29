package com.pubnub.androidapp

import com.pubnub.androidapp.state.Peers
import com.pubnub.androidapp.state.TestMessages
import io.cucumber.java.en.Then
import org.awaitility.Awaitility
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import java.time.Duration

class ReceivedEventStepDefinition(
    private val peers: Peers,
    private val testMessages: TestMessages
) {

    @Then("{peerId} received {eventType} the same as generated")
    fun receiveEvent(peerId: String, eventType: String) {
        val peer = peers.peers[peerId]
        assertThat(testMessages.messages[eventType], Matchers.not(Matchers.isEmptyOrNullString()))

        Awaitility.await()
            .atMost(Duration.ofSeconds(10))
            .untilAsserted {
                synchronized(peer!!.events) {
                    assertThat(peer.events[eventType], Matchers.containsInAnyOrder(testMessages.messages[eventType]))
                }
            }
    }
}