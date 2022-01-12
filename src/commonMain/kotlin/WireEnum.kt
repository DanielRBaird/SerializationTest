@file:OptIn(ExperimentalSerializationApi::class)

package com.dbaird

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = WireEnumSerializer::class)
public sealed class WireEnum<T : Enum<T>> {
    public data class Value<T : Enum<T>>(public val enum: T) : WireEnum<T>()
    public data class Unknown<T : Enum<T>>(public val raw: String) : WireEnum<T>()

    public fun enumValue(default: T): T {
        return when (this) {
            is Value -> enum
            is Unknown -> default
        }
    }
}

@Serializer(forClass = WireEnum::class)
public class WireEnumSerializer<T : Enum<T>>(private val dataSerializer: KSerializer<T>) : KSerializer<WireEnum<T>> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("EncodableEnumSerializer", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: WireEnum<T>) {
        when (value) {
            is WireEnum.Unknown -> encoder.encodeString(value.raw)
            is WireEnum.Value -> {
                dataSerializer.serialize(encoder, value.enum)
            }
        }
    }

    override fun deserialize(decoder: Decoder): WireEnum<T> {
        val string = decoder.decodeString()
        return try {
            val enum = JSON.decodeFromString(dataSerializer, "\"$string\"")
            WireEnum.Value(enum)
        } catch (_: Throwable) {
            WireEnum.Unknown(string)
        }
    }
}
