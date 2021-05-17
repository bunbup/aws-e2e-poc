package com.pubnub.androidapp

class Keys {

    fun provideSubKey(): String {
        return System.getenv("PUBNUB_SUB_KEY")!!
    }

    fun providePubKey(): String {
        return System.getenv("PUBNUB_PUB_KEY")!!
    }
}
