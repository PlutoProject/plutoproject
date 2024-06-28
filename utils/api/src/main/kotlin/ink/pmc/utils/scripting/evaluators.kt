package ink.pmc.utils.scripting

import ink.pmc.utils.platform.utilsLogger
import java.util.logging.Level
import kotlin.script.experimental.api.*
import kotlin.script.experimental.api.ScriptDiagnostic.Severity.*
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
    evaluation: ScriptEvaluationConfiguration.Builder.() -> Unit = {}
): ResultWithDiagnostics<EvaluationResult> {
    return scriptHost.evalWithTemplate<BaseScript>(source, evaluation = evaluation, compilation = compilation)
        .apply { printEvalLogs() }
}

fun evalPaperScript(
    source: SourceCode,
    compilation: ScriptCompilationConfiguration.Builder.() -> Unit = {},
    evaluation: ScriptEvaluationConfiguration.Builder.() -> Unit = {}
): ResultWithDiagnostics<EvaluationResult> {
    return scriptHost.evalWithTemplate<PaperScript>(source, evaluation = evaluation, compilation = compilation)
        .apply { printEvalLogs() }
}

inline fun <reified T : Any> evalCustomScript(
    source: SourceCode,
    noinline compilation: ScriptCompilationConfiguration.Builder.() -> Unit = {},
    noinline evaluation: ScriptEvaluationConfiguration.Builder.() -> Unit = {}
): ResultWithDiagnostics<EvaluationResult> {
    return scriptHost.evalWithTemplate<T>(source, evaluation = evaluation, compilation = compilation)
        .apply { printEvalLogs() }
}