package com.pubnub.androidapp

import io.cucumber.java.en.Then
import org.awaitility.Awaitility
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import java.time.Duration

class ReceivedEventStepDefinition(val peers: Peers,
                                  val testMessages: TestMessages) {

    @Then("__{peerId} received test {eventType}")
    fun receiveEvent(peerId: String, eventType: String) {
        val peer = peers.peers[peerId]
        assertThat(testMessages.messages[eventType], Matchers.not(Matchers.isEmptyOrNullString()))

        Awaitility.await()
            .atMost(Duration.ofSeconds(15))
            .untilAsserted {
                synchronized(peer!!.events) {
                    assertThat(peer.events[eventType], Matchers.containsInAnyOrder(testMessages.messages[eventType]))
                }
            }
    }
}