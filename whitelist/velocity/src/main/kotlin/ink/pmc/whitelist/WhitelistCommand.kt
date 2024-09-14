package ink.pmc.whitelist

import ink.pmc.advkt.component.text
import ink.pmc.advkt.send
import ink.pmc.utils.VelocityCm
import ink.pmc.utils.VelocityCtx
import ink.pmc.utils.concurrent.submitAsync
import ink.pmc.utils.dsl.cloud.sender
import ink.pmc.utils.player.uuid
import ink.pmc.utils.visual.mochaMaroon
import ink.pmc.utils.visual.mochaPink
import ink.pmc.utils.visual.mochaText
import ink.pmc.whitelist.profile.*
import kotlinx.coroutines.delay
import org.incendo.cloud.kotlin.coroutines.extension.suspendingHandler
import org.incendo.cloud.parser.standard.StringParser
import org.koin.java.KoinJavaComponent.getKoin
import kotlin.time.Duration.Companion.seconds

private val repo = getKoin().get<WhitelistRepository>()

fun VelocityCm.whitelist() {
    val builder = commandBuilder("whitelist", "wl").permission("whitelist.command")
    val addBuilder = builder.literal("add")

    command(
        builder
            .literal("remove")
            .required("name", StringParser.stringParser())
            .suspendingHandler {
                val sender = it.sender
                val name = it.get<String>("name")
                val model = repo.findByName(name) ?: run {
                    sender.send {
                        text("名为 ") with mochaMaroon
                        text("$name ") with mochaText
                        text("的玩家未获得白名单") with mochaMaroon
                    }
                    return@suspendingHandler
                }

                repo.deleteById(model.id.uuid)
                sender.send {
                    text("已经移除玩家 ") with mochaMaroon
                    text("${model.rawName} ") with mochaText
                    text("的白名单") with mochaMaroon
                }
            }
    )

    command(
        builder
            .literal("lookup")
            .required("name", StringParser.stringParser())
            .suspendingHandler {
                val sender = it.sender
                val name = it.get<String>("name")
                val model = repo.findByName(name) ?: run {
                    sender.send {
                        text("未查询到名为 ") with mochaMaroon
                        text("$name ") with mochaText
                        text("的玩家") with mochaMaroon
                    }
                    return@suspendingHandler
                }
                sender.send {
                    text("已查询到名为 ") with mochaMaroon
                    text("${model.rawName} ") with mochaText
                    text("的玩家") with mochaMaroon
                }
            }
    )

    command(
        addBuilder
            .literal("mojang")
            .required("name", StringParser.stringParser())
            .suspendingHandler {
                it.handleAdd(it.get("name"), MojangProfileFetcher)
            }
    )

    command(
        addBuilder
            .literal("bedrock")
            .required("name", StringParser.stringParser())
            .suspendingHandler {
                it.handleAdd(it.get("name"), BedrockProfileFetcher)
            }
    )

    command(
        addBuilder
            .literal("offline")
            .required("name", StringParser.stringParser())
            .suspendingHandler {
                it.handleAdd(it.get("name"), OfflineProfileFetcher)
            }
    )
}

private suspend fun VelocityCtx.handleAdd(name: String, fetcher: ProfileFetcher) {
    val fetch = submitAsync<ProfileData?> {
        fetcher.fetch(name)
    }
    sender.send {
        text("正在获取数据，请稍等...") with mochaPink
    }

    delay(10.seconds)
    if (!fetch.isCompleted) {
        sender.send {
            text("数据获取超时，请重试") with mochaMaroon
        }
        return
    }

    val profileData = fetch.await() ?: run {
        sender.send {
            text("未获取到玩家 ") with mochaMaroon
            text("$name ") with mochaText
            text("的档案，请检查玩家名是否正确") with mochaMaroon
        }
        return
    }
    val model = createWhitelistModel(profileData.uuid, profileData.name, fetcher)
    repo.saveOrUpdate(model)
    sender.send {
        text("已为玩家 ") with mochaPink
        text("${profileData.name} ") with mochaText
        text("添加白名单") with mochaPink
    }
}