package ink.pmc.common.member.storage

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId
import org.javers.core.diff.Diff
import org.javers.core.diff.changetype.ValueChange
import org.javers.core.diff.changetype.map.EntryAdded
import org.javers.core.diff.changetype.map.EntryRemoved
import org.javers.core.diff.changetype.map.EntryValueChange
import org.javers.core.diff.changetype.map.MapChange

data class DataContainerStorage(
    @BsonId val objectId: ObjectId,
    var id: Long,
    var owner: Long,
    var createdAt: Long,
    var lastModifiedAt: Long,
    var contents: MutableMap<String, String>,
    var new: Boolean = false
) : Diffable<DataContainerStorage>() {

    override fun applyDiff(diff: Diff): Diffable<DataContainerStorage> {
        diff.changes.filterIsInstance<ValueChange>().forEach {
            when(it.propertyName) {
                "id" -> id = it.right as Long
                "owner" -> owner = it.right as Long
                "createdAt" -> createdAt = it.right as Long
                "lastModifiedAt" -> lastModifiedAt = it.right as Long
            }
        }

        diff.changes.filterIsInstance<MapChange<*>>().forEach { mapChange ->
            mapChange.entryChanges.filterIsInstance<EntryAdded>().forEach {
                contents[it.key as String] = it.value as String
            }

            mapChange.entryChanges.filterIsInstance<EntryValueChange>().forEach {
                contents.replace(it.key as String, it.rightValue as String)
            }

            mapChange.entryChanges.filterIsInstance<EntryRemoved>().forEach {
                contents.remove(it.key)
            }
        }

        return this
    }

}