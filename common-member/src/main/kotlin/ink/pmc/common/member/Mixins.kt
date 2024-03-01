package ink.pmc.common.member

import com.fasterxml.jackson.databind.annotation.JsonDeserialize

@Suppress("UNUSED")
@JsonDeserialize(`as` = MemberImpl::class)
interface MemberMixin

@Suppress("UNUSED")
@JsonDeserialize(`as` = PunishmentImpl::class)
interface PunishmentMixin

@Suppress("UNUSED")
@JsonDeserialize(`as` = CommentImpl::class)
interface CommentMixin

@Suppress("UNUSED")
@JsonDeserialize(`as` = MemberDataImpl::class)
interface MemberDataMixin