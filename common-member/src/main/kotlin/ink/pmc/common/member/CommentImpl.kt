package ink.pmc.common.member

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import ink.pmc.common.member.api.Comment
import java.util.*

class CommentImpl @JsonCreator constructor(
    @JsonProperty("id") override val id: Long,
    @JsonProperty("owner") override val owner: UUID,
    @JsonProperty("creator") override val creator: UUID,
    @JsonProperty("content") override var content: String
) : Comment {

    override val date: Date = Date()
    override var isModified: Boolean = false

}