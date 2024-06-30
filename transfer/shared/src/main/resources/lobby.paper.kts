fun archiveCategoryOpen(category: Category, player: PlayerWrapper<*>) {
    if (category.id != "archived") {
        return
    }

    player.playSound {
        key = Key.key("block.barrel.open")
        source = Source.BLOCK
    }
}

fun archiveCategoryClose(category: Category, player: PlayerWrapper<*>) {
    if (category.id != "archived") {
        return
    }

    player.playSound {
        key = Key.key("block.barrel.close")
        source = Source.BLOCK
    }
}

menuSettings {
    env {
        destinationButton {
            icon = destination.icon
            name = destination.name
            description = destination.description
        }

        categoryButton {
            icon = category.icon
            name = category.name
            description = category.name
        }
    }

    hooks {
        categoryOpen(::archiveCategoryOpen)
        categoryClose(::archiveCategoryClose)
    }
}

menuContent {
    structure (
        "X########",
        "###S#A###",
        "#########"
    )

    ingredient('X', closeButton())
    ingredient('#', placeholder())
    ingredient('S', getDestinationButton("survival"))
    ingredient('A', getCategoryButton("archive"))
}

form {
    // For bedrock players
}
