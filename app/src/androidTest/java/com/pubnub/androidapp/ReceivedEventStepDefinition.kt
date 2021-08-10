package com.pubnub.androidapp

import com.pubnub.androidapp.state.Peers
import com.pubnub.androidapp.state.TestMessages
import io.cucumber.java.en.Then
import junit.framework.Assert.assertNotNull
import junit.framework.Assert.assertTrue
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
                    assertThat(
                        peer.events[eventType],
                        Matchers.containsInAnyOrder(testMessages.messages[eventType])
                    )
                }
            }
    }

    @Then("{peerId} is notified that {peerId} has joined test channel")
    fun userHasJoinedEvent(peerAId: String, peerBId: String) {
        val peer = peers.peers[peerAId]

        Awaitility.await()
            .atMost(Duration.ofSeconds(10))
            .untilAsserted {
                synchronized(peer!!.presence) {
                    assertTrue(peer.presence!!
                        .any { it.event == "join" && it.uuid == peers.peers[peerBId]?.pubnub?.configuration?.uuid })
                }
            }
    }
}
