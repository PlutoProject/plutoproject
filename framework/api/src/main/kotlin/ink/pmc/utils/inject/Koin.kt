package ink.pmc.utils.inject

import org.koin.core.KoinApplication
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.qualifier.Qualifier
import org.koin.dsl.KoinAppDeclaration
import org.koin.java.KoinJavaComponent.getKoin
import org.koin.mp.KoinPlatformTools

fun startKoinIfNotPresent(declaration: KoinAppDeclaration): KoinApplication {
    val context = KoinPlatformTools.defaultContext() as GlobalContext
    val application = context.getKoinApplicationOrNull() ?: return startKoin(declaration)
    return application.apply(declaration)
}

inline fun <reified T> inlinedGet(
    qualifier: Qualifier? = null,
    noinline parameters: ParametersDefinition? = null,
): T {
    return getKoin().get(qualifier, parameters)
}