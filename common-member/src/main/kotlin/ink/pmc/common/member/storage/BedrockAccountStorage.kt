package ink.pmc.common.member.storage

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId
import org.javers.core.diff.Diff
import org.javers.core.diff.changetype.ValueChange
import org.javers.core.metamodel.annotation.DiffIgnore

data class BedrockAccountStorage(
    @BsonId @DiffIgnore var objectId: ObjectId,
    var id: Long,
    var linkedWith: Long,
    var xuid: String,
    var gamertag: String,
    @Transient var new: Boolean = false
) : Diffable<BedrockAccountStorage>() {

    override fun applyDiff(diff: Diff): Diffable<BedrockAccountStorage> {
        diff.changes.filterIsInstance<ValueChange>().forEach {
            when (it.propertyName) {
                "id" -> id = it.right as Long
                "linkedWith" -> linkedWith = it.right as Long
                "xuid" -> xuid = it.right as String
                "gamertag" -> gamertag = it.right as String
            }
        }

        return this
    }

}