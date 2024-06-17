package ink.pmc.transfer

import ink.pmc.utils.multiplaform.player.PlayerWrapper

class PaperTransferService : BaseTransferServiceImpl() {

    override val playerCount: Int
        get() = TODO("Not yet implemented")

    override suspend fun transferPlayer(player: PlayerWrapper<*>, id: String) {
        player.switchServer(id)
    }

    override fun close() {
        TODO("Not yet implemented")
    }

}