package com.pubnub.androidapp.history

import com.pubnub.androidapp.Peers
import com.pubnub.api.endpoints.FetchMessages
import com.pubnub.api.models.consumer.PNBoundedPage
import com.pubnub.api.models.consumer.PNPublishResult
import com.pubnub.api.models.consumer.history.PNFetchMessagesResult
import io.cucumber.java.en.And
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import org.awaitility.Awaitility
import org.awaitility.Awaitility.*
import org.junit.Assert
import org.junit.Assert.*
import java.util.*
import java.util.concurrent.TimeUnit

class FetchHistorySteps(val peers: Peers) {

    lateinit var testMessage: String
    var pnPublishResult: PNPublishResult? = null
    var publishedTimetoken: Long? = null
    var startTimetoken: Long? = null
    val channels: MutableMap<String, String> = mutableMapOf()
    var pnFetchMessagesResult: PNFetchMessagesResult? = null

    @And("_I have a random {channel} channel")
    fun And_I_have_a_random_test_channel(channel: String) {
        channels[channel] = "testChannel_${UUID.randomUUID()}"
    }

    @Given("_Random test message is generated")
    fun Given_Random_test_message_is_generated() {
        testMessage = "randomTestMessage_${UUID.randomUUID()}"
    }

    @And("{peerId} publishes test message to {channel} channel with store flag set")
    fun And_PeerA_publishes_test_message_to_test_channel_with_store_flag_set(peerId: String, channel: String) {
        pnPublishResult = peers.peers[peerId]?.pubnub?.publish(channel = channels[channel]!!, message = testMessage, shouldStore = true)?.sync()
    }

    @And("Timetoken of published message is stored")
    fun And_Timetoken_of_published_message_is_stored() {
        publishedTimetoken = pnPublishResult?.timetoken
    }

    @And("Start time taken from Timetoken is generated")
    fun And_Start_time_taken_from_Timetoken_is_generated() {
        startTimetoken = publishedTimetoken?.minus(1)
    }

    @When("{peerId} fetches the history of {channel} channel including range from start time till present moment")
    fun When_PeerB_fetches_the_history_of_test_channel_including_range_from_start_time_till_present_moment(peerId: String, channel: String) {
        val peer = peers.peers[peerId]

        await().atMost(10, TimeUnit.SECONDS).untilAsserted {
            pnFetchMessagesResult = peer?.pubnub?.fetchMessages(
                channels = listOf(channels[channel]!!),
                page = PNBoundedPage(
                    start = startTimetoken,
                    end = peer.pubnub.time().sync()?.timetoken
                )
            )?.sync()

            assertNotNull(pnFetchMessagesResult)
            assertTrue(true == pnFetchMessagesResult?.channels?.get(channels[channel])?.isNotEmpty())
        }

    }

    @Then("The list of past messages includes test message  for {channel} channel")
    fun Then_The_list_of_past_messages_includes_test_message(channel: String) {
        assertTrue(true == pnFetchMessagesResult?.channels?.get(channels[channel])?.any { it.message.asString == testMessage })
    }
}