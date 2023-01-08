package me.wtbm.nerdystuff.bezierCurves

import me.wtbm.nerdystuff.bezierCurves.BezierTools.ContinuityItem
import me.wtbm.nerdystuff.bezierCurves.BezierTools.RemoveId
import me.wtbm.nerdystuff.bezierCurves.BezierTools.RemoveItem
import me.wtbm.nerdystuff.bezierCurves.BezierTools.continuityId
import me.wtbm.nerdystuff.bezierCurves.BezierTools.addMoveId
import me.wtbm.nerdystuff.bezierCurves.BezierTools.addMoveItem
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack


object BezierToolsListener : Listener {


    @EventHandler(priority = EventPriority.NORMAL) // I know, normal is default, but It's just there, so I don't forget it exists in case I need a lower or higher one
    fun clickItem(event: PlayerInteractEvent){
        val p = event.player
        val action: Action = event.action
        val item: ItemStack = p.inventory.itemInMainHand
        val meta = item.itemMeta ?: return
        if(meta.lore?.isEmpty() ?: return ) return
        val left : Boolean = action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK
        if( !left && !(action == Action.RIGHT_CLICK_BLOCK || action == Action.RIGHT_CLICK_AIR) ) return
        if(meta.customModelData % RemoveId < 100){
            RemoveItem(p,left)
        }
        else if(meta.customModelData % addMoveId < 100){
            addMoveItem(p,left)
        }
        else if(meta.customModelData % continuityId < 100){
            ContinuityItem(p,left)
        }
    }




}