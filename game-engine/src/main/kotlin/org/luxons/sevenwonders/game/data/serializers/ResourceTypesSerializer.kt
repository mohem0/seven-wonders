package org.luxons.sevenwonders.game.data.serializers

import java.lang.reflect.Type

import org.luxons.sevenwonders.game.resources.ResourceType

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer

class ResourceTypesSerializer : JsonSerializer<List<ResourceType>>, JsonDeserializer<List<ResourceType>> {

    override fun serialize(
        resources: List<ResourceType>,
        typeOfSrc: Type,
        context: JsonSerializationContext
    ): JsonElement {
        val s = resources.map { it.symbol }.joinToString("")
        return JsonPrimitive(s)
    }

    @Throws(JsonParseException::class)
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): List<ResourceType> {
        val s = json.asString
        val resources = ArrayList<ResourceType>()
        for (c in s.toCharArray()) {
            resources.add(ResourceType.fromSymbol(c))
        }
        return resources
    }
}
