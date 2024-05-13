package ink.pmc.common.member.storage

import ink.pmc.common.utils.storage.asBson
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.bson.BsonDocument
import org.bson.types.ObjectId
import org.javers.core.diff.Diff
import org.javers.core.diff.changetype.ValueChange
import org.javers.core.diff.changetype.map.EntryAdded
import org.javers.core.diff.changetype.map.EntryRemoved
import org.javers.core.diff.changetype.map.EntryValueChange
import org.javers.core.diff.changetype.map.MapChange
import org.javers.core.metamodel.annotation.DiffIgnore
import org.javers.core.metamodel.annotation.DiffInclude
import org.javers.core.metamodel.annotation.Id
import org.javers.core.metamodel.annotation.TypeName

@Serializable
@TypeName("DataContainer")
data class DataContainerStorage(
    @DiffIgnore @SerialName("_id") @Contextual val objectId: ObjectId,
    @Id var id: Long,
    var owner: Long,
    var createdAt: Long,
    var lastModifiedAt: Long,
    @DiffInclude @Contextual var contents: BsonDocument,
    @Transient var new: Boolean = false
) : Diffable<DataContainerStorage>() {

    override fun applyDiff(diff: Diff): Diffable<DataContainerStorage> {
        diff.changes.filterIsInstance<ValueChange>().forEach {
            when (it.propertyName) {
                "id" -> id = it.right as Long
                "owner" -> owner = it.right as Long
                "createdAt" -> createdAt = it.right as Long
                "lastModifiedAt" -> lastModifiedAt = it.right as Long
            }
        }

        diff.changes.filterIsInstance<MapChange<*>>().forEach { mapChange ->
            mapChange.entryChanges.filterIsInstance<EntryAdded>().forEach {
                contents[it.key as String] = it.value.asBson
            }

            mapChange.entryChanges.filterIsInstance<EntryValueChange>().forEach {
                contents.replace(it.key as String, it.rightValue.asBson)
            }

            mapChange.entryChanges.filterIsInstance<EntryRemoved>().forEach {
                contents.remove(it.key)
            }
        }

        return this
    }

}