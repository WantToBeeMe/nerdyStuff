package me.wtbm.nerdystuff.bezier

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

    fun reCalculate(name: String, interval :Int = 8){
        bezierCurves[name]?.setInterval(interval)
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


}