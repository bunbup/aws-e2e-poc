package com.pubnub.androidapp

import com.pubnub.androidapp.state.Peer
import com.pubnub.androidapp.state.Peers
import com.pubnub.api.PNConfiguration
import com.pubnub.api.PubNub
import io.cucumber.java.en.And

class CreatePubNubInstanceStepDefinition(private val peers: Peers) {

    @And("I create PubNub instance for {peerId}")
    fun I_create_PubNub_instance_for_PeerA(peerId: String) {
        peers.peers[peerId] =
            Peer(pubnub = PubNub(PNConfiguration().apply {
                publishKey = BuildConfig.PUBNUB_PUB_KEY
                subscribeKey = BuildConfig.PUBNUB_SUB_KEY
            }))
    }
}

