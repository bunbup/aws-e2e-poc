package com.pubnub.androidapp.state

import com.pubnub.api.PubNub

data class Peer(
    var pubnub: PubNub,
    val events: MutableMap<String, MutableList<String>> = mutableMapOf()
)