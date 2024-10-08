package ink.pmc.options

import com.mongodb.kotlin.client.coroutine.MongoCollection
import ink.pmc.options.api.OptionsManager
import ink.pmc.options.models.OptionsContainerModel
import ink.pmc.options.repositories.OptionsContainerRepository
import ink.pmc.provider.ProviderService
import org.koin.dsl.module

private const val COLLECTION_NAME = "options_data"

internal fun getCollection(): MongoCollection<OptionsContainerModel> {
    return ProviderService.defaultMongoDatabase.getCollection(COLLECTION_NAME)
}

val commonModule = module {
    single<OptionsContainerRepository> { OptionsContainerRepository(getCollection()) }
    single<OptionsManager> { OptionsManagerImpl() }
}