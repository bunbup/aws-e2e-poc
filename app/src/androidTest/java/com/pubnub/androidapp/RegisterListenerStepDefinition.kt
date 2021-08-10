package com.pubnub.androidapp

import com.pubnub.androidapp.state.Peers
import com.pubnub.api.PubNub
import com.pubnub.api.callbacks.SubscribeCallback
import com.pubnub.api.models.consumer.PNStatus
import com.pubnub.api.models.consumer.pubsub.PNMessageResult
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult
import com.pubnub.api.models.consumer.pubsub.PNSignalResult
import io.cucumber.java.en.Given

class RegisterListenerStepDefinition(private val peers: Peers) {

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
                if (eventType == "signal") {
                    synchronized(peer.events) {
                        peer.events.putIfAbsent(eventType, mutableListOf())
                        val list = peer.events[eventType]
                        list?.add(pnSignalResult.message.asString)
                    }
                }
            }

            override fun message(pubnub: PubNub, pnMessageResult: PNMessageResult) {
                if (eventType == "message") {
                    synchronized(peer.events) {
                        peer.events.putIfAbsent(eventType, mutableListOf())
                        val list = peer.events[eventType]
                        list?.add(pnMessageResult.message.asString)
                    }
                }
            }

            override fun presence(pubnub: PubNub, pnPresenceEventResult: PNPresenceEventResult) {
                if (eventType == "presence") {
                    synchronized(peer.presence) {
                        peer.presence.add(pnPresenceEventResult)
                    }
                }

            }
        })
    }
}