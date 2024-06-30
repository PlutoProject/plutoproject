fun SingleWindowDsl.transferRoot() {
    gui {
        structure(
            "X########",
            "###C#A###",
            "########S"
        )

        ingredient('#', Material.GRAY_STAINED_GLASS_PANE) {
            displayName {
                empty()
            }
        }

        simpleItem('X') {
            provider(Material.RED_STAINED_GLASS_PANE) {
                displayName {
                    text("关闭菜单") without italic() with mochaRed
                }
            }
            onClick {
                window.close()
            }
        }

        simpleItem('C') {
            provider(Material.CAMPFIRE) {
                displayName {
                    text("占位符 ") without italic() with mochaPink
                    text("Placeholder") without italic() with mochaSubtext0
                }
                lore {
                    text("· 在线 ") without italic() with mochaGreen
                    text("0/100") without italic() with mochaText
                }
                lore {
                    empty()
                }
                lore {
                    text("轻量的原版生存服，辅以少量拓展。") without italic() with mochaSubtext0
                }
                lore {
                    text("面向喜爱养老与田园生存的玩家。") without italic() with mochaSubtext0
                }
                lore {
                    empty()
                }
                lore {
                    text("√ 点击以加入") without italic() with mochaFlamingo
                }
            }
        }

        simpleItem('A') {
            provider(Material.BARREL) {
                displayName {
                    text("归档 ") without italic() with mochaBlue
                    text("Archive") without italic() with mochaSubtext0
                }
                lore {
                    empty()
                }
                lore {
                    text("在星社还叫做 PlutoMC 的时候，服务器更换了多次周目。") without italic() with mochaSubtext0
                }
                lore {
                    text("旧的存档被完好的保留，服务器也已摒弃周目机制。") without italic() with mochaSubtext0
                }
                lore {
                    text("是时候将这些存档重新展示，供后人怀旧。") without italic() with mochaSubtext0
                }
                lore {
                    empty()
                }
                lore {
                    text("点击以打开") without italic() with mochaFlamingo
                }
            }
            onClick {
                val player = it.player
                player.playSound {
                    key(Key.key("block.barrel.open"))
                }
                transferArchive()
            }
        }

        simpleItem('S') {
            provider(Material.TRIPWIRE_HOOK) {
                displayName {
                    text("偏好设置") without italic() with mochaYellow
                }
                lore {
                    text("一些简单的设置。") without italic() with mochaSubtext0
                }
                lore {
                    text("比如，你可以选择每次进服时自动传送到某个子服。") without italic() with mochaSubtext0
                }
            }
        }
    }
}

fun SingleWindowDsl.transferArchive() {
    gui {
        structure(
            "XB#######",
            "###1#2###",
            "#########"
        )

        onClose {
            it.playSound {
                key(Key.key("block.barrel.close"))
            }
        }

        ingredient('#', Material.GRAY_STAINED_GLASS_PANE) {
            displayName {
                empty()
            }
        }

        simpleItem('X') {
            provider(Material.RED_STAINED_GLASS_PANE) {
                displayName {
                    text("关闭菜单") without italic() with mochaRed
                }
            }
            onClick {
                window.close()
            }
        }

        simpleItem('B') {
            provider(Material.YELLOW_STAINED_GLASS_PANE) {
                displayName {
                    text("返回上一页") without italic() with mochaYellow
                }
            }
            onClick {
                val player = it.player
                player.playSound {
                    key(Key.key("block.barrel.close"))
                }
                transferRoot()
            }
        }

        simpleItem('1') {
            provider(Material.HEART_OF_THE_SEA) {
                displayName {
                    text("伊始 ") without italic() with mochaSky
                    text("The beginning") without italic() with mochaSubtext0
                }
                lore {
                    text("· 在线 ") without italic() with mochaGreen
                    text("0/20") without italic() with mochaText
                }
                lore {
                    empty()
                }
                lore {
                    text("PlutoMC 一周目时的存档。") without italic() with mochaSubtext0
                }
                lore {
                    text("截止到切换周目，这个存档发展了半年多。") without italic() with mochaSubtext0
                }
                lore {
                    text("工业区层出不穷，内容相当完善。") without italic() with mochaSubtext0
                }
                lore {
                    empty()
                }
                lore {
                    text("√ 点击以加入") without italic() with mochaFlamingo
                }
            }
        }

        simpleItem('2') {
            provider(Material.AMETHYST_CLUSTER) {
                displayName {
                    text("存续 ") without italic() with mochaMauve
                    text("Story continues") without italic() with mochaSubtext0
                }
                lore {
                    text("· 在线 ") without italic() with mochaGreen
                    text("0/20") without italic() with mochaText
                }
                lore {
                    empty()
                }
                lore {
                    text("PlutoMC 二周目时的存档。") without italic() with mochaSubtext0
                }
                lore {
                    text("一周目后期，活跃度降低了不少。") without italic() with mochaSubtext0
                }
                lore {
                    text("简单准备后，服务器切换到了二周目。") without italic() with mochaSubtext0
                }
                lore {
                    text("这个周目发展一段时间后就因外界因素断档了。") without italic() with mochaSubtext0
                }
                lore {
                    text("一些建筑的点位已无从考究。") without italic() with mochaSubtext0
                }
                lore {
                    empty()
                }
                lore {
                    text("√ 点击以加入") without italic() with mochaFlamingo
                }
            }
        }
    }

    changeTitle {
        text("归档")
    }
}

fun transferWindow(player: Player): Window {
    return singleWindow {
        title {
            text("去往何处？")
        }

        viewer = player
        transferRoot()
    }
}