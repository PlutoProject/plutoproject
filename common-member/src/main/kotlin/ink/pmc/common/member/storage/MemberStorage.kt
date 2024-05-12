package ink.pmc.common.member.storage

import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.bson.types.ObjectId
import org.javers.core.diff.Diff
import org.javers.core.diff.changetype.ValueChange
import org.javers.core.diff.changetype.container.ElementValueChange
import org.javers.core.diff.changetype.container.ListChange
import org.javers.core.diff.changetype.container.ValueAdded
import org.javers.core.diff.changetype.container.ValueRemoved
import org.javers.core.metamodel.annotation.DiffIgnore
import org.javers.core.metamodel.annotation.DiffInclude
import org.javers.core.metamodel.annotation.Id
import org.javers.core.metamodel.annotation.TypeName
import java.lang.reflect.Modifier

private val cls = MemberStorage::class.java

@Suppress("UNCHECKED_CAST")
@Serializable
@TypeName("Member")
data class MemberStorage(
    @DiffIgnore @SerialName("_id") @Contextual val objectId: ObjectId,
    @Id var uid: Long,
    var id: String,
    var name: String,
    var rawName: String,
    var whitelistStatus: String,
    var authType: String,
    var createdAt: Long,
    var lastJoinedAt: Long?,
    var lastQuitedAt: Long?,
    @DiffInclude var dataContainer: Long,
    var bedrockAccount: Long?,
    var bio: String?,
    var isHidden: Boolean?,
    @Transient var new: Boolean = false
) : Diffable<MemberStorage>() {

    override fun applyDiff(diff: Diff): Diffable<MemberStorage> {
        diff.changes.filterIsInstance<ValueChange>().forEach {
            val field = cls.getDeclaredField(it.propertyName).apply {
                isAccessible = true
            }

            if (Modifier.isFinal(field.modifiers)) {
                return@forEach
            }

            val diffed = it.right
            field.set(this, diffed)
        }

        diff.changes.filterIsInstance<ListChange>().forEach { containerChange ->
            val field = cls.getDeclaredField(containerChange.propertyName).apply {
                isAccessible = true
            }

            val collection = field.get(this) as MutableList<Any?>

            containerChange.changes.filterIsInstance<ValueAdded>().forEach {
                collection.add(it.index, it.value)
            }

            containerChange.changes.filterIsInstance<ElementValueChange>().forEach {
                collection.add(it.index, it.rightValue)
            }

            containerChange.changes.filterIsInstance<ValueRemoved>().forEach {
                collection.remove(it.index)
            }
        }

        return this
    }

}