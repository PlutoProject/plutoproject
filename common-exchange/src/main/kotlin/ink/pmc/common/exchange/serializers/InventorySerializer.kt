package ink.pmc.common.exchange.serializers

import com.google.gson.*
import ink.pmc.common.exchange.utils.inventoryFromBase64
import ink.pmc.common.exchange.utils.inventoryToBase64
import ink.pmc.common.utils.json.toJsonElement
import ink.pmc.common.utils.json.toObject
import org.bukkit.inventory.Inventory
import java.lang.reflect.Type

object InventorySerializer : JsonSerializer<Inventory> {

    override fun serialize(src: Inventory, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        return inventoryToBase64(src).toJsonElement()
    }

}

object InventoryDeserializer : JsonDeserializer<Inventory> {

    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): Inventory {
        val string = json.toObject<String>()
        return inventoryFromBase64(string)
    }

}