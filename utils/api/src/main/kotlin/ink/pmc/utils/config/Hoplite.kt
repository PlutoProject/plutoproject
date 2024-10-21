package ink.pmc.utils.config

import com.sksamuel.hoplite.ConfigLoaderBuilder
import com.sksamuel.hoplite.ExperimentalHoplite
import ink.pmc.utils.platform.paperDepClassLoader

@OptIn(ExperimentalHoplite::class)
@Suppress("NOTHING_TO_INLINE")
inline fun preconfiguredConfigLoaderBuilder(): ConfigLoaderBuilder {
    return ConfigLoaderBuilder.empty()
        .withClassLoader(paperDepClassLoader)
        .withExplicitSealedTypes()
        .addDefaults()
}