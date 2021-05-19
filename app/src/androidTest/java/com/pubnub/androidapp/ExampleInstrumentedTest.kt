package com.pubnub.androidapp

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.pubnub.api.PNConfiguration
import com.pubnub.api.PubNub
import com.pubnub.api.callbacks.SubscribeCallback
import com.pubnub.api.models.consumer.PNStatus

import org.junit.Test
import org.junit.runner.RunWith

import java.util.*

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    private val pubNubToTest: PubNub = PubNub(PNConfiguration().apply {
        subscribeKey = BuildConfig.PUBNUB_SUB_KEY
        publishKey = BuildConfig.PUBNUB_PUB_KEY
    })

    @Test
    fun useAppContext() {

        // Context of the app under test.
        val uuid = UUID.randomUUID()
        val channelName = "channelName-$uuid"
        val content = "content-$uuid"
        pubNubToTest.subscribe(channels = listOf(channelName))
        pubNubToTest.addListener(object : SubscribeCallback() {
            override fun status(pubnub: PubNub, pnStatus: PNStatus) {
                //ignore
            }
        })

        pubNubToTest.publish(channel = channelName, message = content).sync()
    }
}