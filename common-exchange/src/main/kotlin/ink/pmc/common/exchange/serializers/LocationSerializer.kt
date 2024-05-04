package ink.pmc.common.exchange.serializers

import com.google.gson.*
import org.bukkit.Bukkit
import org.bukkit.Location
import java.lang.reflect.Type


object LocationSerializer : JsonSerializer<Location> {

    override fun serialize(src: Location, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        return JsonObject().apply {
            addProperty("world", src.world.name)
            addProperty("x", src.x)
            addProperty("y", src.y)
            addProperty("z", src.z)
            addProperty("yaw", src.yaw)
            addProperty("pitch", src.pitch)
        }
    }

}

object LocationDeserializer : JsonDeserializer<Location> {

    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): Location {
        val jsonObject = json.asJsonObject
        val world = Bukkit.getServer().getWorld(jsonObject["world"].asString)!!
        val x = jsonObject["x"].asDouble
        val y = jsonObject["y"].asDouble
        val z = jsonObject["z"].asDouble
        val yaw = jsonObject["yaw"].asFloat
        val pitch = jsonObject["pitch"].asFloat

        return Location(world, x, y, z, yaw, pitch)
    }

}