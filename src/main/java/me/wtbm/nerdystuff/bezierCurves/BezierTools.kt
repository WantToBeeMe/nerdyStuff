package me.wtbm.nerdystuff.bezierCurves

import me.wtbm.nerdystuff.bezierCurves.BezierSplineController.getSplines
import me.wtbm.nerdystuff.bezierCurves.BezierSplineController.removeLast
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack

object BezierTools {
    val continuityId = 800
    val RemoveId = 700
    val moveMakeId = 600
    val lg = "${ChatColor.GRAY}"
    val dg = "${ChatColor.DARK_GRAY}"
    val ye = "${ChatColor.YELLOW}"
    val title : (String)-> String = {name -> "${BezierCommand.title}${ChatColor.GRAY} ($name)${ChatColor.WHITE}"}

    //Left: correct point
    //Right: change continuity
    fun ContinuityItem(p: Player, left: Boolean){
        val item = p.inventory.itemInMainHand
        val itemMeta = item.itemMeta
        val name : String = itemMeta?.lore!!.last().split('[',']')[1]
        val value = itemMeta.customModelData%continuityId
        if(value == 0){
            if(!left){
                itemMeta.setDisplayName("${ChatColor.DARK_PURPLE}${ChatColor.BOLD}Continuity Mode${ChatColor.RESET}$lg (Aligned)")
                itemMeta.setCustomModelData(continuityId+1)
                item.itemMeta = itemMeta
                item.type = Material.BLAZE_ROD
                p.inventory.setItemInMainHand(item)
                p.sendMessage("${title(name)} change mode to$ye Aligned")
                return
            }
            p.sendMessage("${title(name)} not implemented yet 0")
        }
        if(value == 1){
            if(!left){
                itemMeta.setDisplayName("${ChatColor.DARK_PURPLE}${ChatColor.BOLD}Continuity Mode${ChatColor.RESET}$lg (Mirrored)")
                itemMeta.setCustomModelData(continuityId+2)
                item.itemMeta = itemMeta
                item.type = Material.AMETHYST_SHARD
                p.inventory.setItemInMainHand( item)
                p.sendMessage("${title(name)} change mode to$ye Mirrored")
                return
            }
            p.sendMessage("${title(name)} not implemented yet 1")

        }
        else{
            if(!left){
                itemMeta.setDisplayName("${ChatColor.DARK_PURPLE}${ChatColor.BOLD}Continuity Mode${ChatColor.RESET}$lg (Connected)")
                itemMeta.setCustomModelData(continuityId)
                item.itemMeta = itemMeta
                item.type = Material.STICK
                p.inventory.setItemInMainHand( item)
                p.sendMessage("${title(name)} change mode to$ye Connected")
                return
            }
            p.sendMessage("${title(name)} not implemented yet 2")
        }
    }

    //Left: move Point
    //Right: Add curve
    fun addMoveItem(p: Player, left:Boolean){
        val item = p.inventory.itemInMainHand
        val itemMeta = item.itemMeta
        val name : String = itemMeta?.lore!!.last().split('[',']')[1]
        val value = itemMeta.customModelData%moveMakeId
        var continuity = Continuity.MIRRORED
            run loop@{
                p.inventory.contents.forEach contin@{ stack ->
                    val meta = stack.itemMeta ?: return@contin
                    if (meta.lore?.isEmpty() ?: return@contin) return@contin
                    if( meta.lore!!.last().split('[',']')[1] != name )return@contin

                    if (meta.customModelData % continuityId == 0) {
                        continuity = Continuity.CONNECTED
                        return@loop
                    }
                    else if (meta.customModelData % continuityId == 1) {
                        continuity = Continuity.ALIGNED
                        return@loop
                    }
                    else if (meta.customModelData % continuityId == 2) {
                        continuity = Continuity.MIRRORED
                        return@loop
                    }
                }
        }
        if(left){
            if(isHoldingMoveMake(p)){
                val loc = getSplines()[name]?.lookingAtWhatLoc(p)
                if(loc != null){
                    itemMeta.setDisplayName("${ChatColor.WHITE}${ChatColor.BOLD}Move${ChatColor.RESET}$lg (C:${loc.first} - P:${loc.second})")
                    itemMeta.setCustomModelData(moveMakeId+1)
                    itemMeta.lore = (listOf("${dg}Left:$lg on control point to correct the mode","${dg}Right:$lg change mode","${dg}Spline:$lg [$name] <${loc.first},${loc.second}>"))
                    item.itemMeta = itemMeta
                    item.type = Material.SPECTRAL_ARROW
                    p.inventory.setItemInMainHand( item)
                    p.sendMessage("${title(name)} Moving Point: $ye${loc.second} ${ChatColor.WHITE}from Curve: $ye${loc.first}")
                    return
                }
                else{
                    p.sendMessage("${title(name)} there is no ControlPoint there to move")
                }
            }
            else if(isUsingMoveMake(p) != null){
                itemMeta.setDisplayName("${ChatColor.WHITE}${ChatColor.BOLD}Move/Make${ChatColor.RESET}$lg (select control point)")
                itemMeta.setCustomModelData(moveMakeId)
                itemMeta.lore = (listOf("${dg}Left:$lg on control point to correct the mode","${dg}Right:$lg change mode","${dg}Spline:$lg [$name]"))
                item.itemMeta = itemMeta
                item.type = Material.ARROW
                p.inventory.setItemInMainHand( item)
                p.sendMessage("${title(name)} stopped moving")
                return
            }
            else{
                p.sendMessage("${title(name)} something went wrong, it seems this spline doesn't exist anymore")
            }
        }
        else{
            if(BezierSplineController.addCurveToSpline(name,continuity)) {
                p.sendMessage("${title(name)} added new curve segment")
            }
            else p.sendMessage("${title(name)} something went wrong, it seems this spline doesn't exist anymore")
        }

    }

    //Left: Remove last
    //Right: ??
    fun RemoveItem(p: Player, left:Boolean){
        val item = p.inventory.itemInMainHand
        val itemMeta = item.itemMeta
        val name : String = itemMeta?.lore!!.last().split('[',']')[1]
        val value = itemMeta.customModelData%RemoveId

        if(left){
            if(removeLast(name)) p.sendMessage("${title(name)} removed last segment of the spline")
            else p.sendMessage("${title(name)} cant remove anymore")

            itemMeta?.setDisplayName("${ChatColor.RED}${ChatColor.BOLD}Remove${ChatColor.RESET}$lg (${getSplines().size})")
        }
        else{
            p.sendMessage("${title(name)} nothing yet")
        }
    }

    fun getMovePoint(p: Player): Pair<Int,Int>?{
        val item = p.inventory.itemInMainHand
        val itemMeta = item.itemMeta
        val pair : String = itemMeta?.lore!!.last().split('<','>')[1]
        val curve : Int? = pair.split(',')[0].toIntOrNull()
        val point : Int? = pair.split(',')[1].toIntOrNull()
        if(curve == null || point == null) return null
        return Pair(curve,point)
    }

    fun isUsingMoveMake(p: Player): String?{
        val item: ItemStack = p.inventory.itemInMainHand
        val meta = item.itemMeta ?: return null
        if(meta.lore?.isEmpty() ?: return null ) return null
        if(meta.customModelData%moveMakeId == 1)
            return meta.lore!!.last().split('[',']')[1]
        return null
    }

    fun isHoldingMoveMake(p: Player) : Boolean
    {
        val item: ItemStack = p.inventory.itemInMainHand
        val meta = item.itemMeta ?: return false
        if(meta.lore?.isEmpty() ?: return false ) return false
        return meta.customModelData == moveMakeId
    }

    fun giveTools(p: Player, name:String){
        val create = ItemStack(Material.AMETHYST_SHARD, 1)
        create.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, 1)
        val createMeta = create.itemMeta
        createMeta?.addItemFlags(ItemFlag.HIDE_ENCHANTS)
        createMeta?.setDisplayName("${ChatColor.DARK_PURPLE}${ChatColor.BOLD}Continuity Mode${ChatColor.RESET}$lg (Mirrored)")
        createMeta?.lore = (listOf("${dg}Left:$lg on control point to correct the mode","${dg}Right:$lg change mode","${dg}Spline:$lg [$name]"))
        createMeta?.setCustomModelData(continuityId+2)
        create.itemMeta = createMeta

        val move = ItemStack(Material.ARROW, 1)
        move.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, 1)
        val moveMeta = move.itemMeta
        moveMeta?.addItemFlags(ItemFlag.HIDE_ENCHANTS)
        moveMeta?.setDisplayName("${ChatColor.WHITE}${ChatColor.BOLD}Move/Make${ChatColor.RESET}$lg (select control point)")
        moveMeta?.lore = (listOf("${dg}Left:$lg on control point to start moving it","${dg}Right:$lg add a curve to the end of the spline","${dg}Spline:$lg [$name]"))
        moveMeta?.setCustomModelData(moveMakeId)
        move.itemMeta = moveMeta

        val undo = ItemStack(Material.ECHO_SHARD, 1)
        undo.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, 1)
        val undoMeta = undo.itemMeta
        undoMeta?.addItemFlags(ItemFlag.HIDE_ENCHANTS)
        undoMeta?.setDisplayName("${ChatColor.RED}${ChatColor.BOLD}Remove${ChatColor.RESET}$lg (${getSplines().size})")
        undoMeta?.lore = (listOf("${dg}Left:$lg remove last curve from the spline","${dg}Spline:$lg [$name]"))
        undoMeta?.setCustomModelData(RemoveId)
        undo.itemMeta = undoMeta

        p.inventory.addItem(create, move, undo)
    }
}