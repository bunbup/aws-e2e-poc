package com.pubnub.androidapp

import com.pubnub.api.PNConfiguration
import com.pubnub.api.PubNub
import io.cucumber.java.en.And
import java.util.*

class CreatePubNubInstanceStepDefinition(val peers: Peers) {

    @And("__I create PubNub instance for {peerId}")
    fun I_create_PubNub_instance_for_PeerA(peerId: String) {
        peers.peers[peerId] = Peer(pubnub = PubNub(PNConfiguration().apply {
            publishKey = BuildConfig.PUBNUB_PUB_KEY
            subscribeKey = BuildConfig.PUBNUB_SUB_KEY
        }))
    }
}

data class Peers(val peers: MutableMap<String, Peer> = mutableMapOf())

data class Peer(
    var pubnub: PubNub,
    val events: MutableMap<String, MutableList<String>> = mutableMapOf()
)

data class TestMessages(val messages: MutableMap<String, String> = mutableMapOf())
class TestChannel() {
    val testChannel: String = "channel-${UUID.randomUUID()}"
}
