package com.pubnub.androidapp

import com.pubnub.api.PNConfiguration
import com.pubnub.api.PubNub
import com.pubnub.api.callbacks.SubscribeCallback
import com.pubnub.api.enums.PNStatusCategory
import com.pubnub.api.models.consumer.PNStatus
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult
import io.cucumber.java.en.And
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import org.awaitility.Awaitility
import org.awaitility.Awaitility.*
import org.junit.Assert
import org.junit.Assert.*
import java.util.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class PresenceSteps {
    lateinit var pubKey: String
    lateinit var subKey: String

    private val peers: MutableMap<String, PubNub> = mutableMapOf()
    private val channels: MutableMap<String, String> = mutableMapOf()

    private val presenceEventResultsPerPeer: MutableMap<String, MutableList<PNPresenceEventResult>> = mutableMapOf()

    @Given("_I have a valid publish key")
    fun Given_I_have_a_valid_publish_key() {
        pubKey = "demo-36"
    }

    @And("_I have a valid subscribe key")
    fun And_I_have_a_valid_subscribe_key() {
        subKey = "demo-36"
    }

    @And("_I create PubNub instance for {peerId}")
    fun And_I_create_PubNub_instance_for(peerId: String) {
        peers[peerId] = PubNub(PNConfiguration().apply {
            subscribeKey = subKey
            publishKey = pubKey
            uuid = "$peerId-${UUID.randomUUID()}"
        })
        presenceEventResultsPerPeer[peerId] = mutableListOf()
    }

    @Then("{peerId} is notified that {peerId} has joined channel {channel}")
    fun Then_PeerA_is_notified_that_PeerB_has_joined_test_channel(peerA: String, peerB: String, channel: String) {
        await().atMost(10, TimeUnit.SECONDS).untilAsserted {
            assertNotNull(presenceEventResultsPerPeer[peerA])
            assertTrue(presenceEventResultsPerPeer[peerA]!!.isNotEmpty())

            assertTrue(presenceEventResultsPerPeer[peerA]!!
                .any { it.channel == channels[channel] && it.event == "join" && it.uuid == peers[peerB]?.configuration?.uuid})
        }
    }

    @When("{peerId} subscribes to channel {channel}")
    fun When_Peer_subscribes_to_test_channel(peerId: String, channel: String) {
        peers[peerId]?.subscribe(channels = listOf(channels[channel]!!))
    }

    @And("_{peerId}'s subscription is up and running")
    fun And_Peers_subscription_is_up_and_running(peerId: String) {
        val connected = CountDownLatch(1)
        peers[peerId]?.addListener(object : SubscribeCallback() {
            override fun status(pubnub: PubNub, pnStatus: PNStatus) {
                if (!pnStatus.error && pnStatus.category == PNStatusCategory.PNConnectedCategory) {
                    connected.countDown()
                }
            }
        })
        connected.await(5, TimeUnit.SECONDS)
    }

    @And("{peerId} subscribes to channel {channel} including presence changes")
    fun And_Peer_subscribes_to_test_channel_including_presence_changes(peerId: String, channel: String) {
        peers[peerId]?.subscribe(channels = listOf(channels[channel]!!), withPresence = true)
    }

    @Given("{peerId} registers listener for presence changes")
    fun And_Peer_registers_listener_for_presence_changes(peerId: String) {
        peers[peerId]?.addListener(object : SubscribeCallback() {
            override fun presence(pubnub: PubNub, pnPresenceEventResult: PNPresenceEventResult) {
                presenceEventResultsPerPeer[peerId]?.let {
                    it.add(pnPresenceEventResult)
                }

                println(presenceEventResultsPerPeer)
            }

            override fun status(pubnub: PubNub, pnStatus: PNStatus) {
            }
        })
    }

    @And("I have a random channel {channel}")
    fun And_I_have_a_random_channel_test_channel(channel: String) {
        channels[channel] = "${channel}_${UUID.randomUUID()}"
    }
}