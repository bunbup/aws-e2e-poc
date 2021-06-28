package com.pubnub.androidapp.presence

import io.cucumber.junit.CucumberOptions

@CucumberOptions(features = ["features"], strict = false, glue = ["com.pubnub.androidapp.presence.steps"])
@SuppressWarnings("unused")
class PNCucumberOptions