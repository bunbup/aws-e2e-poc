package com.pubnub.androidapp

import com.pubnub.androidapp.state.Peers
import com.pubnub.androidapp.state.TestChannel
import com.pubnub.androidapp.state.TestMessages
import com.pubnub.androidapp.state.Timetoken
import com.pubnub.api.models.consumer.PNBoundedPage
import com.pubnub.api.models.consumer.history.PNFetchMessagesResult
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import org.awaitility.Awaitility
import org.junit.Assert
import java.util.concurrent.TimeUnit

class HistoryMessagesStepDefinition(
    private val peers: Peers,
    private val testChannel: TestChannel,
    private val startTimetoken: Timetoken,
    private val testMessages: TestMessages
) {
    var pnFetchMessagesResult: PNFetchMessagesResult? = null

    @When("{peerId} fetches the history of {channel} channel including range from start time till present moment")
    fun When_PeerB_fetches_the_history_of_test_channel_including_range_from_start_time_till_present_moment(
        peerId: String,
        channel: String
    ) {
        val peer = peers.peers[peerId]

        Awaitility.await().atMost(10, TimeUnit.SECONDS).untilAsserted {
            pnFetchMessagesResult = peer?.pubnub?.fetchMessages(
                channels = listOf(testChannel.testChannel),
                page = PNBoundedPage(
                    start = startTimetoken.timetoken,
                    end = peer.pubnub.time().sync()?.timetoken
                )
            )?.sync()

            Assert.assertNotNull(pnFetchMessagesResult)
            Assert.assertTrue(
                true == pnFetchMessagesResult?.channels?.get(testChannel.testChannel)?.isNotEmpty()
            )
        }

    }

    @Then("The list of past messages includes test message for test channel")
    fun Then_The_list_of_past_messages_includes_test_message() {
        Assert.assertTrue(
            true == pnFetchMessagesResult?.channels?.get(testChannel.testChannel)
                ?.any { it.message.asString == testMessages.messages["message"] })
    }
}