package com.pubnub.androidapp.test

import io.cucumber.core.api.TypeRegistry
import io.cucumber.core.api.TypeRegistryConfigurer
import io.cucumber.cucumberexpressions.ParameterType
import io.cucumber.cucumberexpressions.Transformer
import java.util.*
import java.util.Locale.ENGLISH


class TypeRegistryConfiguration : TypeRegistryConfigurer {
    override fun locale(): Locale {
        return ENGLISH
    }

    override fun configureTypeRegistry(typeRegistry: TypeRegistry) {
        typeRegistry.defineParameterType(
            ParameterType<String>(
                "channel",
                "(\\S+)",
                String::class.java,
                Transformer<String?> { text -> text })
        )
        typeRegistry.defineParameterType(
            ParameterType<String>(
                "msg",
                "(\\S+)",
                String::class.java,
                Transformer<String?> { text -> text })
        )
        typeRegistry.defineParameterType(
            ParameterType<String>(
                "peerId",
                "(\\S+)",
                String::class.java,
                Transformer<String?> { text -> text})
        )
    }
}