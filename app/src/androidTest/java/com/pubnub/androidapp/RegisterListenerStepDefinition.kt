package com.pubnub.androidapp

import com.pubnub.api.PNConfiguration
import com.pubnub.api.PubNub
import com.pubnub.api.callbacks.SubscribeCallback
import com.pubnub.api.models.consumer.PNStatus
import com.pubnub.api.models.consumer.pubsub.PNSignalResult
import io.cucumber.java.en.Given

class RegisterListenerStepDefinition(val peers: Peers) {

    @Given("{peerId} registers listener for {eventType}")
    fun registerListenerForSignals(peerId: String, eventType: String){
        val peer = peers.peers[peerId]
        peer?.pubnub?.addListener(object : SubscribeCallback() {
            override fun status(pubnub: PubNub, pnStatus: PNStatus) {
                synchronized(peer.events) {
                    peer.events.putIfAbsent("status", mutableListOf())
                    val list = peer.events["status"]
                    list?.add(pnStatus.category.name)
                }
            }

            override fun signal(pubnub: PubNub, pnSignalResult: PNSignalResult) {
                synchronized(peer.events) {
                    peer.events.putIfAbsent(eventType, mutableListOf())
                    val list = peer.events[eventType]
                    list?.add(pnSignalResult.message.asString)
                }
            }
        })
    }
}