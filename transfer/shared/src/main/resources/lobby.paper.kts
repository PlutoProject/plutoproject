menu {
    main {
        structure(
            "X########",
            "###S#A###",
            "########S"
        )

        background('#')
        closeButton('X')
        destination("survival", 'S')
        category("archive", 'A')
        settings('S')
    }

    category("archive") {
        structure(
            "XB#######",
            "###1#2###",
            "#########"
        )

        background('#')
        closeButton('X')
        backButton('B')
        destination("plutomc_1", '1')
        destination("plutomc_2", '2')

        onOpen {
            it.playSound {
                key(Key.key("block.barrel.open"))
            }
        }

        onClose {
            it.playSound {
                key(Key.key("block.barrel.open"))
            }
        }
    }
}