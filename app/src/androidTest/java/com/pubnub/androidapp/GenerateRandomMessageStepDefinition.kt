package com.pubnub.androidapp

import com.pubnub.androidapp.state.TestMessages
import io.cucumber.java.en.And
import java.util.*

class GenerateRandomMessageStepDefinition(private val testMessages: TestMessages) {

    @And("Random {eventType} content is generated")
    fun generateRandomMessage(eventType: String) {
        testMessages.messages[eventType] = "$eventType-${UUID.randomUUID()}"
    }
}