package com.pubnub.androidapp.publishsubscribe

import io.cucumber.junit.CucumberOptions

@CucumberOptions(features = ["features"], strict = false, glue = ["com.pubnub.androidapp.publishsubscribe.steps"])
@SuppressWarnings("unused")
class PNCucumberOptions