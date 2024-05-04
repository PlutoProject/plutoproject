package ink.pmc.common.exchange.serializers

import com.google.gson.*
import org.bukkit.NamespacedKey
import org.bukkit.Registry
import org.bukkit.potion.PotionEffect
import java.lang.reflect.Type

object PotionEffectSerializer : JsonSerializer<PotionEffect> {

    override fun serialize(src: PotionEffect, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        return JsonObject().apply {
            addProperty("amplifier", src.amplifier)
            addProperty("duration", src.duration)
            addProperty("namespace", src.type.key.namespace)
            addProperty("key", src.type.key.key)
            addProperty("ambient", src.isAmbient)
            addProperty("particles", src.hasParticles())
            addProperty("icon", src.hasIcon())
        }
    }

}

object PotionEffectDeserializer : JsonDeserializer<PotionEffect> {

    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): PotionEffect {
        val jsonObject = json.asJsonObject
        val amplifier = jsonObject["amplifier"].asInt
        val duration = jsonObject["duration"].asInt
        val namespace = jsonObject["namespace"].asString
        val key = jsonObject["key"].asString
        val type = Registry.POTION_EFFECT_TYPE.get(NamespacedKey(namespace, key))!!
        val ambient = jsonObject["ambient"].asBoolean
        val particles = jsonObject["particles"].asBoolean
        val icon = jsonObject["icon"].asBoolean
        return PotionEffect(type, duration, amplifier, ambient, particles, icon)
    }

}