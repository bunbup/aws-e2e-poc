package com.pubnub.androidapp

import androidx.test.espresso.matcher.ViewMatchers.assertThat
import com.pubnub.api.PNConfiguration
import com.pubnub.api.PubNub
import com.pubnub.api.callbacks.SubscribeCallback
import com.pubnub.api.enums.PNLogVerbosity
import com.pubnub.api.models.consumer.PNStatus
import com.pubnub.api.models.consumer.pubsub.PNMessageResult
import io.cucumber.java.After
import io.cucumber.java.Before
import io.cucumber.java.en.And
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import org.hamcrest.Matchers
import org.junit.Assert
import java.util.*

class PublishSubscribeSteps {

    private val pubnub: PubNub by lazy {
        PubNub(PNConfiguration().apply {
            subscribeKey = BuildConfig.PUBNUB_SUB_KEY
            publishKey = BuildConfig.PUBNUB_PUB_KEY
            logVerbosity = PNLogVerbosity.BODY
        })

    }
    private val channelSubscriptions: MutableMap<String, MutableList<PNMessageResult>> = Collections.synchronizedMap(
        mutableMapOf())

    @After
    fun after() {
        pubnub.unsubscribeAll()
        pubnub.forceDestroy()
    }

    @Given("I have a pubnub instance")
    fun Given_I_have_a_pubnub_instance() {
        assertThat(pubnub, Matchers.notNullValue())
    }

    @When("I subscribe to {channel}")
    fun When_I_subscribe_to_channel(channel: String) {
        pubnub.addListener(object : SubscribeCallback() {
            override fun status(pubnub: PubNub, pnStatus: PNStatus) {
                //
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
        pubnub.subscribe(channels = listOf(channel))
        Thread.sleep(200)
    }

    @And("I publish {channel} to {msg}")
    fun And_I_publish_msg_to_channel(msg: String, channel: String) {
        pubnub.publish(channel = channel, message = msg).sync()
    }

    @Then("On {channel} I should receive {msg}")
    fun Then_on_channel_I_should_receive_msg(channel: String, msg: String) {
        var received = false
        repeat(40) {
            synchronized(channelSubscriptions) {
                received = channelSubscriptions[channel]?.any { it.message.asString == msg } ?: false
            }
            if (!received) {
                Thread.sleep(500)
            }
        }
        if (!received) {
            Assert.fail("The message $msg was not receive on $channel during 5s")
        }
    }
}
