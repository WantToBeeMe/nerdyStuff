package me.wtbm.nerdystuff.old_bezier

import me.wtbm.nerdystuff.old_bezier.OldBezierCurveController.putLoc
import me.wtbm.nerdystuff.old_bezier.OldBezierToolsController.reworkItemMake
import me.wtbm.nerdystuff.old_bezier.OldBezierToolsController.reworkItemMove
import me.wtbm.nerdystuff.old_bezier.OldBezierToolsController.reworkItemUndo
import me.wtbm.nerdystuff.old_bezier.OldBezierCurveController.undoLast
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack


object OldBezierListener : Listener {


    @EventHandler(priority = EventPriority.NORMAL) // I know, normal is default, but It's just there, so I don't forget it exists in case I need a lower or higher one
    fun clickItem(event: PlayerInteractEvent){
        val p = event.player
        val item: ItemStack = p.inventory.itemInMainHand
        val meta = item.itemMeta ?: return
        if(meta.lore?.isEmpty() ?: return ) return
        if(meta.customModelData == OldBezierCurveController.createId){
            val name : String = meta.lore!![0].split('[',']')[1]
            putLoc(name,p.location)
            reworkItemMake(p)
            p.sendMessage("create for $name")
        }
        else if(meta.customModelData == OldBezierCurveController.undoId){
            val name : String = meta.lore!![0].split('[',']')[1]
            reworkItemUndo(p)
            undoLast(name)
            p.sendMessage("undo for $name")
        }
        else if(meta.customModelData == OldBezierCurveController.moveId){
            val name : String = meta.lore!![0].split('[',']')[1]
            reworkItemMove(p)
            p.sendMessage("move for $name")
        }
    }




}