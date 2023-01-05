package me.wtbm.nerdystuff.bezier

import me.wtbm.nerdystuff.NerdyStuff
import me.wtbm.nerdystuff.bezier.BezierCurveController
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack


object BezierToolsController {
    val plugin get() = NerdyStuff.instance

    fun toolsTick(){
        Bukkit.getOnlinePlayers().forEach(){p->
            val item: ItemStack = p.inventory.itemInMainHand
            val meta = item.itemMeta ?: return
            if(meta.lore?.isEmpty() ?: return ) return
            if(meta.customModelData == BezierCurveController.createId){
                val name : String = meta.lore!![0].split('[',']')[1]
                BezierCurveController.bezierCurves[name]?.generateLocListWithExtra(p.location)
            }
        }
    }

    fun reworkItemMake(p: Player){
        val item = p.inventory.itemInMainHand
        val itemMeta = item?.itemMeta
        val name : String = itemMeta?.lore!![0].split('[',']')[1]
        itemMeta.setDisplayName("${ChatColor.LIGHT_PURPLE}${ChatColor.BOLD}Create ${ChatColor.RESET}${ChatColor.GRAY}(${BezierCurveController.bezierCurves[name]?.getPivotAmount()})")
        item.itemMeta = itemMeta
        p.inventory.setItemInMainHand( item)
    }

    fun reworkItemUndo(p: Player){
        val item = p.inventory.itemInMainHand
        val itemMeta = item?.itemMeta
        val name : String = itemMeta?.lore!![0].split('[',']')[1]
        itemMeta.setDisplayName("${ChatColor.RED}${ChatColor.BOLD}Undo ${ChatColor.RESET}${ChatColor.GRAY}(${BezierCurveController.bezierCurves[name]?.getPivotAmount()})")
        item.itemMeta = itemMeta
        p.inventory.setItemInMainHand(item)
    }

    fun reworkItemMove(p: Player){

    }


    fun giveTools(p: Player, name:String){
        val create = ItemStack(Material.AMETHYST_SHARD, 1)
        create.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, 1)
        val createMeta = create.itemMeta
        createMeta?.addItemFlags(ItemFlag.HIDE_ENCHANTS)
        createMeta?.setDisplayName("${ChatColor.LIGHT_PURPLE}${ChatColor.BOLD}Create ${ChatColor.RESET}${ChatColor.GRAY}(${BezierCurveController.bezierCurves[name]?.getPivotAmount()})")
        createMeta?.lore = (listOf("${ChatColor.DARK_GRAY}For creating new pivots for${ChatColor.GRAY} [$name]"))
        createMeta?.setCustomModelData(BezierCurveController.createId)
        create.itemMeta = createMeta

        val move = ItemStack(Material.ARROW, 1)
        move.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, 1)
        val moveMeta = move.itemMeta
        moveMeta?.addItemFlags(ItemFlag.HIDE_ENCHANTS)
        moveMeta?.setDisplayName("${ChatColor.WHITE}${ChatColor.BOLD}Move ${ChatColor.RESET}${ChatColor.GRAY}(select pivot)")
        moveMeta?.lore = (listOf("${ChatColor.DARK_GRAY}For moving existing pivots for${ChatColor.GRAY} [$name]"))
        moveMeta?.setCustomModelData(BezierCurveController.moveId)
        move.itemMeta = moveMeta

        val undo = ItemStack(Material.RED_DYE, 1)
        undo.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, 1)
        val undoMeta = undo.itemMeta
        undoMeta?.addItemFlags(ItemFlag.HIDE_ENCHANTS)
        undoMeta?.setDisplayName("${ChatColor.RED}${ChatColor.BOLD}Undo ${ChatColor.RESET}${ChatColor.GRAY}(${BezierCurveController.bezierCurves[name]?.getPivotAmount()})")
        undoMeta?.lore = (listOf("${ChatColor.DARK_GRAY}For undoing the last pivots for${ChatColor.GRAY} [$name]"))
        undoMeta?.setCustomModelData(BezierCurveController.undoId)
        undo.itemMeta = undoMeta
        p.inventory.addItem(create, move, undo)
    }

}