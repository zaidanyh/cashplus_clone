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

class JsonArrayConverter<E>: TypeAdapterFactory {
    override fun <T : Any?> create(gson: Gson?, type: TypeToken<T>): TypeAdapter<T?>? {
        if (!MutableList::class.java.isAssignableFrom(type.rawType!!)) return null
        val elementType = resolveTypeArgument(type.type)
        val elementTypeAdapter = gson?.getAdapter(TypeToken.get(elementType)) as TypeAdapter<E?>
        return JsonArrayConverter<E?>(elementTypeAdapter).nullSafe() as TypeAdapter<T?>
    }

    private class JsonArrayConverter<E>(
        private val elementTypeAdapter: TypeAdapter<E?>
    ): TypeAdapter<List<E>?>() {
        override fun write(out: JsonWriter?, value: List<E>?) {
            throw UnsupportedOperationException()
        }

        @Throws(IOException::class)
        override fun read(`in`: JsonReader?): List<E>? {
            val list: MutableList<E> = ArrayList()
            when(val token: JsonToken = `in`!!.peek()) {
                JsonToken.BEGIN_ARRAY -> {
                    `in`.beginArray()
                    while (`in`.hasNext()) {
                        elementTypeAdapter.read(`in`)?.let { list.add(it) }
                    }
                    `in`.endArray()
                }
                JsonToken.STRING -> `in`.skipValue()
                JsonToken.NULL -> throw AssertionError("Must never happen: check if the type adapter configured with .nullSafe()")
                JsonToken.NAME, JsonToken.END_ARRAY,
                JsonToken.END_OBJECT, JsonToken.END_DOCUMENT -> throw MalformedJsonException("Unexpected token: $token")
                else -> throw AssertionError("Must never happen: $token")
            }
            return list.ifEmpty { null }
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