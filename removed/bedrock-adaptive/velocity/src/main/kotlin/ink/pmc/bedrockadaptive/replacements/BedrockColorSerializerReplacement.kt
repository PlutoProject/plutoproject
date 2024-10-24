package ink.pmc.bedrockadaptive.replacements

import ink.pmc.bedrockadaptive.delegations.BedrockSerializerDelegation
import ink.pmc.bedrockadaptive.utils.messageTranslatorClass
import ink.pmc.framework.utils.bedrock.bedrockFormats
import ink.pmc.framework.utils.jvm.byteBuddy
import net.bytebuddy.asm.Advice
import net.bytebuddy.description.method.MethodDescription
import net.bytebuddy.dynamic.loading.ClassReloadingStrategy
import net.bytebuddy.matcher.ElementMatchers.named
import net.bytebuddy.matcher.ElementMatchers.takesArguments
import net.kyori.adventure.text.serializer.legacy.CharacterAndFormat
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer

private val formats = run {
    val list = mutableListOf<CharacterAndFormat>()
    list.addAll(CharacterAndFormat.defaults())
    list.addAll(bedrockFormats)
    list
}
val bedrockSerializer = LegacyComponentSerializer.legacySection().toBuilder()
    .formats(bedrockFormats)
    .build()

object BedrockColorSerializerReplacement {

    fun init() {
        byteBuddy
            .redefine(messageTranslatorClass)
            .visit(
                Advice.to(BedrockSerializerDelegation.CovertMessage::class.java)
                    .on(named<MethodDescription>("convertMessage").and(takesArguments(2)))
            )
            .visit(
                Advice.to(BedrockSerializerDelegation.ConvertToJavaMessage::class.java)
                    .on(named("convertToJavaMessage"))
            )
            .make()
            .load(messageTranslatorClass.classLoader, ClassReloadingStrategy.fromInstalledAgent())
    }

}