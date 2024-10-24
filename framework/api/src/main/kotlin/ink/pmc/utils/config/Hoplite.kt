package ink.pmc.utils.config

import com.sksamuel.hoplite.ConfigLoaderBuilder
import com.sksamuel.hoplite.ExperimentalHoplite
import ink.pmc.framework.frameworkClassLoader

@OptIn(ExperimentalHoplite::class)
@Suppress("NOTHING_TO_INLINE")
inline fun preconfiguredConfigLoaderBuilder(): ConfigLoaderBuilder {
    return ConfigLoaderBuilder.empty()
        .withClassLoader(frameworkClassLoader)
        .withExplicitSealedTypes()
        .addDefaults()
}