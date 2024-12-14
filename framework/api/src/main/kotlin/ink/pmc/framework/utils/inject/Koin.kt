package ink.pmc.framework.utils.inject

import org.koin.core.Koin
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

// IDE 只补全在 compose 包下的那个 getKoin()，加一个这个方便些
inline val koin: Koin
    get() = getKoin()