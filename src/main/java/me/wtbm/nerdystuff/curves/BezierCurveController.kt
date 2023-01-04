package me.wtbm.nerdystuff.curves

import me.wtbm.nerdystuff.NerdyStuff
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import java.util.HashMap

object BezierCurveController {
    val plugin get() = NerdyStuff.instance
    val visibleForPlayers : MutableMap<Player, Part> = HashMap()
    var bezierCurves: MutableMap<String, BezierCurve> = HashMap()
    val createId = 6234265
    val undoId = 4234265
    val moveId = 3234265
    //val createSId = "${ChatColor.YELLOW}${ChatColor.GOLD}${ChatColor.YELLOW}${ChatColor.GOLD}${ChatColor.YELLOW}${ChatColor.GOLD}${ChatColor.RED}${ChatColor.BLACK}${ChatColor.RED}${ChatColor.LIGHT_PURPLE}"
    //val moveSId   = "${ChatColor.YELLOW}${ChatColor.GOLD}${ChatColor.YELLOW}${ChatColor.GOLD}${ChatColor.YELLOW}${ChatColor.GOLD}${ChatColor.RED}${ChatColor.BLACK}${ChatColor.RED}${ChatColor.YELLOW}"



    fun generatePath(name: String, block: Material = Material.STONE) : Boolean{
        val contains = bezierCurves.containsKey(name)
        if (contains){
            bezierCurves[name]?.placePath(block)
            bezierCurves.remove(name)
            return true
        }
        return false
    }

    fun putLoc(name: String, location: Location): Boolean{
        val contains = bezierCurves.containsKey(name)
        if(contains)
            bezierCurves[name]?.addLoc(location)
        else
            bezierCurves[name] = BezierCurve(location)
        return contains
    }

    fun reCalculate(name: String, spread :Int = 8){
        bezierCurves[name]?.setSpread(spread)
        bezierCurves[name]?.generateLocList()
    }

    fun undoLast(name: String) : Boolean{
        return bezierCurves[name]?.undoLast() ?: false
    }

    fun showCurves(){
        bezierCurves.forEach(){ curve ->
            visibleForPlayers.forEach(){p ->
                curve.value.showCurve(p.key, p.value)
            }
        }
    }

    fun reworkItemMake(p: Player){
        val item = p.inventory.itemInMainHand
        val itemMeta = item?.itemMeta
        val name : String = itemMeta?.lore!![0].split('[',']')[1]
        itemMeta.setDisplayName("${ChatColor.LIGHT_PURPLE}${ChatColor.BOLD}Create ${ChatColor.RESET}${ChatColor.GRAY}(${bezierCurves[name]?.getPivotAmount()})")
        item.itemMeta = itemMeta
        p.inventory.setItemInMainHand( item)
    }

    fun reworkItemUndo(p: Player){
        val item = p.inventory.itemInMainHand
        val itemMeta = item?.itemMeta
        val name : String = itemMeta?.lore!![0].split('[',']')[1]
        itemMeta.setDisplayName("${ChatColor.RED}${ChatColor.BOLD}Undo ${ChatColor.RESET}${ChatColor.GRAY}(${bezierCurves[name]?.getPivotAmount()})")
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
        createMeta?.setDisplayName("${ChatColor.LIGHT_PURPLE}${ChatColor.BOLD}Create ${ChatColor.RESET}${ChatColor.GRAY}(${bezierCurves[name]?.getPivotAmount()})")
        createMeta?.lore = (listOf("${ChatColor.DARK_GRAY}For creating new pivots for${ChatColor.GRAY} [$name]"))
        createMeta?.setCustomModelData(createId)
        create.itemMeta = createMeta

        val move = ItemStack(Material.ARROW, 1)
        move.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, 1)
        val moveMeta = move.itemMeta
        moveMeta?.addItemFlags(ItemFlag.HIDE_ENCHANTS)
        moveMeta?.setDisplayName("${ChatColor.WHITE}${ChatColor.BOLD}Move ${ChatColor.RESET}${ChatColor.GRAY}(select pivot)")
        moveMeta?.lore = (listOf("${ChatColor.DARK_GRAY}For moving existing pivots for${ChatColor.GRAY} [$name]"))
        moveMeta?.setCustomModelData(moveId)
        move.itemMeta = moveMeta

        val undo = ItemStack(Material.RED_DYE, 1)
        undo.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, 1)
        val undoMeta = undo.itemMeta
        undoMeta?.addItemFlags(ItemFlag.HIDE_ENCHANTS)
        undoMeta?.setDisplayName("${ChatColor.RED}${ChatColor.BOLD}Undo ${ChatColor.RESET}${ChatColor.GRAY}(${bezierCurves[name]?.getPivotAmount()})")
        undoMeta?.lore = (listOf("${ChatColor.DARK_GRAY}For undoing the last pivots for${ChatColor.GRAY} [$name]"))
        undoMeta?.setCustomModelData(undoId)
        undo.itemMeta = undoMeta
        p.inventory.addItem(create, move, undo)
    }

}