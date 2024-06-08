package ink.pmc.member.storage

import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.bson.types.ObjectId
import org.javers.core.diff.Diff
import org.javers.core.diff.changetype.ValueChange
import org.javers.core.metamodel.annotation.DiffIgnore
import org.javers.core.metamodel.annotation.Id
import org.javers.core.metamodel.annotation.TypeName

@Serializable
@TypeName("BedrockAccount")
data class BedrockAccountBean(
    @DiffIgnore @SerialName("_id") @Contextual var objectId: ObjectId,
    @Id var id: Long,
    var linkedWith: Long,
    var xuid: String,
    var gamertag: String,
    @Transient var new: Boolean = false
) : Diffable<BedrockAccountBean>() {

    override fun applyDiff(diff: Diff): Diffable<BedrockAccountBean> {
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