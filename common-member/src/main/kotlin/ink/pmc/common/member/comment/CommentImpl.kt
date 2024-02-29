package ink.pmc.common.member.comment

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import ink.pmc.common.member.api.comment.Comment
import java.util.*

data class CommentImpl @JsonCreator constructor(
    @JsonProperty("id") override val id: Long,
    @JsonProperty("owner") override val owner: UUID,
    @JsonProperty("creator") override val creator: UUID,
    @JsonProperty("content") override var content: String? = null,
    @JsonProperty("createTime") override val createTime: Date = Date(),
    @JsonProperty("isModified") override var isModified: Boolean = false
) : Comment