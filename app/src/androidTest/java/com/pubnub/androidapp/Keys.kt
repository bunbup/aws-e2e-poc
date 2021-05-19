package com.pubnub.androidapp

import android.os.Bundle
import androidx.test.platform.app.InstrumentationRegistry


class Keys {
    private val extras: Bundle = InstrumentationRegistry.getArguments()!!

    private val subKey by lazy {
        extras.getString("PUBNUB_SUB_KEY")!!
    }

    private val pubKey by lazy {
        extras.getString("PUBNUB_PUB_KEY")!!
    }


    fun provideSubKey(): String = subKey

    fun providePubKey(): String = pubKey

}
