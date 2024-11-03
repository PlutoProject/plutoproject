package ink.pmc.framework.utils.config

import com.sksamuel.hoplite.ConfigLoaderBuilder
import com.sksamuel.hoplite.ExperimentalHoplite
import ink.pmc.framework.runtimeClassLoader

@OptIn(ExperimentalHoplite::class)
@Suppress("NOTHING_TO_INLINE")
inline fun preconfiguredConfigLoaderBuilder(): ConfigLoaderBuilder {
    return ConfigLoaderBuilder.empty()
        .withClassLoader(runtimeClassLoader)
        .withExplicitSealedTypes()
        .addDefaults()
}