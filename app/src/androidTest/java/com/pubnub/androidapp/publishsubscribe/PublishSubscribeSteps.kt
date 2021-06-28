package com.pubnub.androidapp.publishsubscribe

import androidx.test.espresso.matcher.ViewMatchers.assertThat
import com.pubnub.androidapp.BuildConfig
import com.pubnub.api.PNConfiguration
import com.pubnub.api.PubNub
import com.pubnub.api.callbacks.SubscribeCallback
import com.pubnub.api.enums.PNLogVerbosity
import com.pubnub.api.enums.PNStatusCategory
import com.pubnub.api.models.consumer.PNStatus
import com.pubnub.api.models.consumer.pubsub.PNMessageResult
import io.cucumber.java.en.And
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import org.awaitility.Awaitility.await
import org.hamcrest.Matchers
import java.time.Duration
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean

class PublishSubscribeSteps  {
    private val pubKey = BuildConfig.PUBNUB_PUB_KEY
    private val subKey = BuildConfig.PUBNUB_SUB_KEY
    private val peerAPubnub by lazy {
        PubNub(PNConfiguration().apply {
            publishKey = pubKey
            subscribeKey = subKey
        })
    }
    private val peerBPubnub by lazy {
        PubNub(PNConfiguration().apply {
            publishKey = pubKey
            subscribeKey = subKey
            logVerbosity = PNLogVerbosity.BODY
        })
    }
    private val channel = "channel-${UUID.randomUUID()}"
    private val subscribed = AtomicBoolean(false)
    private val message = "message"
    private val channelSubscriptions: MutableMap<String, MutableList<PNMessageResult>> = Collections.synchronizedMap(
        mutableMapOf())



    @Given("I have a valid publish key")
    fun I_have_a_valid_publish_key() {
        assertThat(pubKey, Matchers.not(Matchers.isEmptyOrNullString()))

    }

    @And("I have a valid subscribe key")
    fun I_have_a_valid_subscribe_key() {
        assertThat(subKey, Matchers.not(Matchers.isEmptyOrNullString()))
    }

    @And("I create PubNub instance for PeerA")
    fun I_create_PubNub_instance_for_PeerA() {
        assertThat(peerAPubnub, Matchers.notNullValue())
    }

    @And("I create PubNub instance for PeerB")
    fun I_create_PubNub_instance_for_PeerB() {
        assertThat(peerBPubnub, Matchers.notNullValue())
    }

    @And("I have a random channel test channel")
    fun I_have_a_random_channel_test_channel() {
        assertThat(channel, Matchers.not(Matchers.isEmptyOrNullString()))
    }

    @Given("PeerB registers listener for incoming messages")
    fun PeerB_registers_listener_for_incoming_messages() {
        peerBPubnub.addListener(object : SubscribeCallback() {
            override fun status(pubnub: PubNub, pnStatus: PNStatus) {
                if (pnStatus.category == PNStatusCategory.PNConnectedCategory) {
                    subscribed.set(true)
                }

            }

            override fun message(pubnub: PubNub, pnMessageResult: PNMessageResult) {
                synchronized(channelSubscriptions) {
                    val messageResults = Collections.synchronizedList(mutableListOf<PNMessageResult>())

                    if (pnMessageResult.channel == channel) {
                        messageResults.add(pnMessageResult)
                    }
                    channelSubscriptions.putIfAbsent(channel, messageResults)
                }
            }
        })

    }

    @And("PeerB subscribes to test channel")
    fun PeerB_subscribes_to_test_channel() {
        peerBPubnub.subscribe(channels = listOf(channel))
    }

    @And("PeerB's subscription is up and running")
    fun PeerBs_subscription_is_up_and_running() {
        await()
            .atMost(Duration.ofSeconds(5))
            .untilAsserted {
                assertThat(subscribed.get(), Matchers.`is`(true))
            }
    }

    @And("Random message test message is generated")
    fun Random_message_test_message_is_generated() {

    }

    @When("PeerA publishes test message to test channel")
    fun PeerA_publishes_test_message_to_test_channel() {
        peerAPubnub.publish(channel = channel, message = message).sync()
    }

    @Then("PeerB in notified that message has arrived")
    fun PeerB_in_notified_that_message_has_arrived() {

    }

    @And("Received message is same as test message")
    fun Received_message_is_same_as_test_message() {
        await()
            .atMost(Duration.ofSeconds(10))
            .untilAsserted {
                assertThat(channelSubscriptions[channel]?.any { it.message.asString == message }, Matchers.`is`(true))
            }

    }

}