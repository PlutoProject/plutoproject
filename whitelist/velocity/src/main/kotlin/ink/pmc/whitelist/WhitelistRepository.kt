package ink.pmc.whitelist

import com.mongodb.kotlin.client.coroutine.MongoCollection

class WhitelistRepository(private val collection: MongoCollection<WhitelistModel>) {
}