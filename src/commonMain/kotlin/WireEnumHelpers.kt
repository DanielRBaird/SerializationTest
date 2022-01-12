package com.dbaird

import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import kotlin.native.concurrent.ThreadLocal

/**
 * Common pre-configured JSON for general use
 */
@ThreadLocal
public val JSON: Json = Json {
    encodeDefaults = false
    ignoreUnknownKeys = true
}

internal inline fun <reified T : Any> serializeObject(obj: T): String {
    return JSON.encodeToString(serializer(), obj)
}

internal inline fun <reified T : Any> deserializeObject(jsonString: String): T {
    return JSON.decodeFromString<T>(serializer(), jsonString)
}