package com.pasukanlangit.id.travelling.datasource.utils

import com.google.gson.Gson
import com.google.gson.TypeAdapter
import com.google.gson.TypeAdapterFactory
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import com.google.gson.stream.MalformedJsonException
import java.io.IOException
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

class JsonObjectConverter<E>: TypeAdapterFactory {

    override fun <T : Any?> create(gson: Gson?, type: TypeToken<T>): TypeAdapter<T?>? {
        if (!Any::class.java.isAssignableFrom(type.rawType!!)) return null
        val elementType = resolveTypeArgument(type.type)
        val elementTypeAdapter = gson?.getAdapter(TypeToken.get(elementType)) as TypeAdapter<E?>
        return JsonObjectConverter<E?>(elementTypeAdapter).nullSafe() as TypeAdapter<T?>
    }

    private class JsonObjectConverter<E>(
        private val elementTypeAdapter: TypeAdapter<E?>
    ): TypeAdapter<E?>() {
        override fun write(out: JsonWriter?, value: E?) {
            throw UnsupportedOperationException()
        }

        @Throws(IOException::class)
        override fun read(`in`: JsonReader?): E? {
            var deserializer: E? = null
            when(val token: JsonToken = `in`!!.peek()) {
                JsonToken.BEGIN_OBJECT -> {
                    `in`.beginObject()
                    deserializer = elementTypeAdapter.read(`in`)
                    `in`.endObject()
                }
                JsonToken.STRING -> `in`.skipValue()
                JsonToken.NULL -> throw AssertionError("Must never happen: check if the type adapter configured with .nullSafe()")
                JsonToken.NAME, JsonToken.END_ARRAY,
                JsonToken.END_OBJECT, JsonToken.END_DOCUMENT -> throw MalformedJsonException("Unexpected token: $token")
                else -> throw AssertionError("Must never happen: $token")
            }

            return deserializer
        }

    }

    companion object {
        private fun resolveTypeArgument(type: Type): Type {
            if (type !is ParameterizedType) {
                return Any::class.java
            }
            val parameterizedType: ParameterizedType = type
            return parameterizedType.actualTypeArguments.first()
        }
    }
}