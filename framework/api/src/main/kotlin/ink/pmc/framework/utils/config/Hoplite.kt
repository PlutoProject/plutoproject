package ink.pmc.framework.utils.config

import com.sksamuel.hoplite.ConfigLoaderBuilder
import com.sksamuel.hoplite.ExperimentalHoplite
import ink.pmc.framework.runtimeClassLoader
import ink.pmc.framework.utils.config.decoder.ComponentDecoder

@OptIn(ExperimentalHoplite::class)
@Suppress("NOTHING_TO_INLINE")
inline fun preconfiguredConfigLoaderBuilder(): ConfigLoaderBuilder {
    return ConfigLoaderBuilder.empty()
        .withClassLoader(runtimeClassLoader)
        .withExplicitSealedTypes()
        .addDefaults()
        .addDecoder(ComponentDecoder)
}