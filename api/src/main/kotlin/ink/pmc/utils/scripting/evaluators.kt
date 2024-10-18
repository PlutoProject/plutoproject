package ink.pmc.utils.scripting

import ink.pmc.utils.platform.utilsLogger
import java.util.logging.Level
import kotlin.script.experimental.api.*
import kotlin.script.experimental.api.ScriptDiagnostic.Severity.*
import kotlin.script.experimental.jvm.updateClasspath
import kotlin.script.experimental.jvm.util.classpathFromClassloader
import kotlin.script.experimental.jvmhost.BasicJvmScriptingHost

val scriptHost = BasicJvmScriptingHost()

fun ResultWithDiagnostics<*>.printEvalLogs() {
    fun ScriptDiagnostic.Severity.toLogLevel() = when (this) {
        DEBUG -> Level.INFO
        INFO -> Level.INFO
        WARNING -> Level.WARNING
        ERROR -> Level.SEVERE
        FATAL -> Level.SEVERE
    }

    this.reports.forEach {
        utilsLogger.log(it.severity.toLogLevel(), "[Script Evaluator] ${it.message}", it.exception)
    }
}

fun evalBaseScript(
    source: SourceCode,
    compilation: ScriptCompilationConfiguration.Builder.() -> Unit = {},
    evaluation: ScriptEvaluationConfiguration.Builder.() -> Unit = {},
    classLoader: ClassLoader = Thread.currentThread().contextClassLoader
): ResultWithDiagnostics<EvaluationResult> {
    val saveClassLoader = Thread.currentThread().contextClassLoader
    Thread.currentThread().contextClassLoader = classLoader
    return scriptHost.evalWithTemplate<BaseScript>(source, evaluation = evaluation, compilation = compilation)
        .apply {
            Thread.currentThread().contextClassLoader = saveClassLoader
            printEvalLogs()
        }
}

fun evalPaperScript(
    source: SourceCode,
    compilation: ScriptCompilationConfiguration.Builder.() -> Unit = {},
    evaluation: ScriptEvaluationConfiguration.Builder.() -> Unit = {},
    classLoader: ClassLoader = Thread.currentThread().contextClassLoader
): ResultWithDiagnostics<EvaluationResult> {
    val saveClassLoader = Thread.currentThread().contextClassLoader
    Thread.currentThread().contextClassLoader = classLoader
    return scriptHost.evalWithTemplate<PaperScript>(source, evaluation = evaluation, compilation = compilation)
        .apply {
            Thread.currentThread().contextClassLoader = saveClassLoader
            printEvalLogs()
        }
}

fun evalVelocityScript(
    source: SourceCode,
    compilation: ScriptCompilationConfiguration.Builder.() -> Unit = {},
    evaluation: ScriptEvaluationConfiguration.Builder.() -> Unit = {},
    classLoader: ClassLoader = Thread.currentThread().contextClassLoader
): ResultWithDiagnostics<EvaluationResult> {
    val saveClassLoader = Thread.currentThread().contextClassLoader
    Thread.currentThread().contextClassLoader = classLoader
    return scriptHost.evalWithTemplate<VelocityScript>(source, evaluation = evaluation, compilation = compilation)
        .apply {
            Thread.currentThread().contextClassLoader = saveClassLoader
            printEvalLogs()
        }
}

inline fun <reified T : Any> evalCustomScript(
    source: SourceCode,
    noinline compilation: ScriptCompilationConfiguration.Builder.() -> Unit = {},
    noinline evaluation: ScriptEvaluationConfiguration.Builder.() -> Unit = {},
    classLoader: ClassLoader = Thread.currentThread().contextClassLoader
): ResultWithDiagnostics<EvaluationResult> {
    val saveClassLoader = Thread.currentThread().contextClassLoader
    Thread.currentThread().contextClassLoader = classLoader
    return scriptHost.evalWithTemplate<T>(source, evaluation = evaluation, compilation = compilation)
        .apply {
            Thread.currentThread().contextClassLoader = saveClassLoader
            printEvalLogs()
        }
}

fun ScriptCompilationConfiguration.Builder.importClasspath(classLoader: ClassLoader) {
    updateClasspath(classpathFromClassloader(classLoader))
}