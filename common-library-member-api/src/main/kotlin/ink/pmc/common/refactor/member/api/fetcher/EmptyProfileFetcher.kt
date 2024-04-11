package ink.pmc.common.refactor.member.api.fetcher

import java.util.*

@Suppress("UNUSED")
object EmptyProfileFetcher : ProfileFetcher {

    override suspend fun fetch(name: String): UUID? = null

    override suspend fun validate(name: String, uuid: UUID): Boolean = false

    override suspend fun validate(name: String, uuid: String): Boolean = false

}