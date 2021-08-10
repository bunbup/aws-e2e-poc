package com.pubnub.androidapp

import com.pubnub.androidapp.state.Peers
import com.pubnub.androidapp.state.TestChannel
import com.pubnub.androidapp.state.TestMessages
import com.pubnub.androidapp.state.Timetoken
import io.cucumber.java.en.And

class PublishMessageWithStoreFlag(
    private val peers: Peers,
    private val testChannel: TestChannel,
    private val testMessages: TestMessages,
    private val startTimetoken: Timetoken
) {

    private lateinit var timetoken: Timetoken
    @And("{peerId} publishes test message to test channel with store flag set")
    fun publishMessageWith(peerId: String) {
        val res = peers.peers[peerId]?.pubnub?.publish(channel = testChannel.testChannel, message = testMessages.messages["message"]!!, shouldStore = true)?.sync()
        timetoken = Timetoken().apply { timetoken = res?.timetoken!! }
    }

    @And("Start time taken from Timetoken is generated")
    fun And_Start_time_taken_from_Timetoken_is_generated() {
        startTimetoken.timetoken = timetoken.timetoken - 1
    }

}