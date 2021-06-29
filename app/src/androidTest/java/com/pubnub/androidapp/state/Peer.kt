package com.pubnub.androidapp.state

import com.pubnub.api.PubNub
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult

data class Peer(
    var pubnub: PubNub,
    val events: MutableMap<String, MutableList<String>> = mutableMapOf(),
    val presence: MutableList<PNPresenceEventResult> = mutableListOf()
)