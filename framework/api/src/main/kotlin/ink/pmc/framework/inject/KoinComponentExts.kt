package ink.pmc.framework.inject

import org.koin.core.component.KoinComponent
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.qualifier.Qualifier

@Suppress("UNUSED")
inline fun <reified T : Any> KoinComponent.getOrNull(
    qualifier: Qualifier? = null,
    noinline parameters: ParametersDefinition? = null,
): T? {
    return getKoin().getOrNull<T>(qualifier, parameters)
}