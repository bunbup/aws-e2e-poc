package com.pubnub.androidapp

import androidx.test.espresso.matcher.ViewMatchers
import com.pubnub.api.enums.PNStatusCategory
import io.cucumber.java.en.And
import org.awaitility.Awaitility.await
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import java.time.Duration

class SubscribeStepDefinition(val peers: Peers, val testChannel: TestChannel) {

    @And("__{peerId} subscribes to test channel")
    fun SubscribeToTestChannel(peerId: String) {
        val peer = peers.peers[peerId]
        peer?.pubnub?.subscribe(channels = listOf(testChannel.testChannel))
        await()
            .atMost(Duration.ofSeconds(5))
            .untilAsserted {
                assertThat(peer!!.events["status"], Matchers.containsInAnyOrder(PNStatusCategory.PNConnectedCategory.name))
            }
    }
}