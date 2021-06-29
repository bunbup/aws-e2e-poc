package com.pubnub.androidapp

import com.pubnub.androidapp.state.Peers
import com.pubnub.api.enums.PNStatusCategory
import io.cucumber.java.en.And
import org.awaitility.Awaitility
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import java.time.Duration

class SubscriptionUpAndRunningStepDefinition(private val peers: Peers) {

    @And("{peerId}'s subscription is up and running")
    fun subscriptionUpAndRunning(peerId: String) {
        Awaitility.await()
            .atMost(Duration.ofSeconds(5))
            .untilAsserted {
                MatcherAssert.assertThat(
                    peers.peers[peerId]!!.events["status"],
                    Matchers.containsInAnyOrder(PNStatusCategory.PNConnectedCategory.name)
                )
            }
    }
}