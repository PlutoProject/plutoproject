package ink.pmc.common.exchange.lobby

import com.destroystokyo.paper.event.player.PlayerAdvancementCriterionGrantEvent
import ink.pmc.common.exchange.CHECKOUT_FAILED_TICKETS_NOT_ENOUGH
import ink.pmc.common.exchange.CHECKOUT_SUCCEED
import ink.pmc.common.exchange.EXCHANGE_BYPASS_PERMISSION
import ink.pmc.common.exchange.exchangeService
import ink.pmc.common.exchange.proto.lobby2proxy.itemDistributeNotify
import ink.pmc.common.exchange.proto.server2lobby.exchangeEnd
import ink.pmc.common.exchange.utils.*
import ink.pmc.common.member.api.paper.member
import ink.pmc.common.utils.chat.replace
import ink.pmc.common.utils.proto.player.player
import ink.pmc.common.utils.visual.mochaMaroon
import ink.pmc.common.utils.visual.mochaText
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import org.bukkit.event.EventHandler
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.inventory.InventoryCreativeEvent
import org.bukkit.event.player.*
import org.bukkit.inventory.EquipmentSlot

@Suppress("UNUSED")
object PlayerActionHandler {

    private fun handleEvent(event: PlayerEvent) {
        if (event !is Cancellable) {
            return
        }

        val player = event.player

        if (player.hasPermission(EXCHANGE_BYPASS_PERMISSION)) {
            return
        }

        event.isCancelled = true
    }

    @EventHandler
    fun playerTeleportEvent(event: PlayerTeleportEvent) {
        handleEvent(event)
    }

    @EventHandler
    fun playerRecipeDiscoverEvent(event: PlayerRecipeDiscoverEvent) {
        handleEvent(event)
    }

    @EventHandler
    fun playerAdvancementCriterionGrantEvent(event: PlayerAdvancementCriterionGrantEvent) {
        handleEvent(event)
    }

    @EventHandler
    fun inventoryCreativeEvent(event: InventoryCreativeEvent) {
        val player = if (event.whoClicked !is Player) {
            return
        } else {
            event.whoClicked as Player
        }

        if (player.hasPermission(EXCHANGE_BYPASS_PERMISSION)) {
            return
        }

        val material = event.cursor.type

        if (isForbiddenItem(event.cursor)) {
            return
        }

        if (isMaterialAvailable(material) || material == Material.AIR) {
            return
        }

        event.cursor = getForbiddenItem(material)
    }

    @EventHandler
    fun blockPlaceEvent(event: BlockPlaceEvent) {
        val player = event.player

        if (player.hasPermission(EXCHANGE_BYPASS_PERMISSION)) {
            return
        }

        event.isCancelled = true
    }

    @EventHandler
    fun blockBreakEvent(event: BlockBreakEvent) {
        val player = event.player

        if (player.hasPermission(EXCHANGE_BYPASS_PERMISSION)) {
            return
        }

        event.isCancelled = true
    }

    @EventHandler
    fun playerDropItemEvent(event: PlayerDropItemEvent) {
        handleEvent(event)
    }

    @EventHandler
    suspend fun playerInteractEvent(event: PlayerInteractEvent) {
        val player = event.player
        val member = player.member()

        if (player.hasPermission(EXCHANGE_BYPASS_PERMISSION)) {
            return
        }

        event.isCancelled = true

        if (event.hand == EquipmentSlot.HAND
            && event.action == Action.RIGHT_CLICK_BLOCK
            && event.clickedBlock != null
            && isCheckoutSign(event.clickedBlock!!)
        ) {
            val cart = cart(player)
            val price = cart.size.toLong()

            if (!exchangeService.noLessThan(member, price)) {
                player.sendMessage(
                    CHECKOUT_FAILED_TICKETS_NOT_ENOUGH.replace(
                        "<amount>", Component.text(price).color(
                            mochaMaroon
                        )
                    )
                )
                return
            }

            player.sendMessage(CHECKOUT_SUCCEED.replace("<amount>", Component.text(price).color(mochaText)))
            clearInventory(player)

            lobbyExchangeService.stub.endExchange(exchangeEnd {
                serviceId = lobbyExchangeService.id.toString()
                this.player = player {
                    username = player.name
                    uuid = player.uniqueId.toString()
                }
            })

            lobbyExchangeService.stub.notifyItemDistribute(itemDistributeNotify {
                serviceId = lobbyExchangeService.id.toString()
                this.player = player {
                    username = player.name
                    uuid = player.uniqueId.toString()
                }
                cost = price
                item.encodeItems(cart)
            })
        }
    }

}