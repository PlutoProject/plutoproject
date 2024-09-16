package ink.pmc.playerdb

import ink.pmc.playerdb.api.PlayerDb
import ink.pmc.provider.ProviderService
import org.koin.dsl.module

private const val COLLECTION_NAME = "player_database"
private val mongoCollection = ProviderService.defaultMongoDatabase.getCollection<DatabaseModel>(COLLECTION_NAME)

val sharedModule = module {
    single { DatabaseRepository(mongoCollection) }
    single<PlayerDb> { PlayerDbImpl() }
}