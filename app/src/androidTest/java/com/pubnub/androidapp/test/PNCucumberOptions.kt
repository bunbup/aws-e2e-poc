package com.pubnub.androidapp.test

import io.cucumber.junit.CucumberOptions

@CucumberOptions(features = ["features"], strict = true, glue = ["com.pubnub.androidapp"])
@SuppressWarnings("unused")
class PNCucumberOptions