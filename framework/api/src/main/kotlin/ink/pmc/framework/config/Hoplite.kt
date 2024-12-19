package ink.pmc.framework.config

import com.sksamuel.hoplite.ConfigLoaderBuilder
import com.sksamuel.hoplite.ExperimentalHoplite
import ink.pmc.framework.runtimeClassLoader
import ink.pmc.framework.config.decoder.CharDecoder
import ink.pmc.framework.config.decoder.ComponentDecoder

@OptIn(ExperimentalHoplite::class)
@Suppress("NOTHING_TO_INLINE")
inline fun preconfiguredConfigLoaderBuilder(): ConfigLoaderBuilder {
    return ConfigLoaderBuilder.empty()
        .withClassLoader(runtimeClassLoader)
        .withExplicitSealedTypes()
        .addDefaults()
        .addDecoder(ComponentDecoder)
        .addDecoder(CharDecoder)
}