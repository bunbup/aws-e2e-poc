package com.pubnub.androidapp

import com.pubnub.androidapp.state.TestChannel
import io.cucumber.java.en.And
import java.util.*

class GenerateRandomChannel(private val testChannel: TestChannel) {

    @And("I have a random channel")
    fun generateRandomChannel() {
        testChannel.testChannel = "channel-${UUID.randomUUID()}"
    }
}