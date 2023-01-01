package me.wtbm.nerdystuff.curves

import me.wtbm.nerdystuff.NerdyStuff
import me.wtbm.nerdystuff.curves.BezierCurveController.bezierCurves
import me.wtbm.nerdystuff.curves.BezierCurveController.generatePath
import me.wtbm.nerdystuff.curves.BezierCurveController.giveTools
import me.wtbm.nerdystuff.curves.BezierCurveController.putLoc
import me.wtbm.nerdystuff.curves.BezierCurveController.reCalculate
import me.wtbm.nerdystuff.curves.BezierCurveController.undoLast
import me.wtbm.nerdystuff.curves.BezierCurveController.visibleForPlayers
import org.bukkit.ChatColor
import org.bukkit.Color
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player
import java.util.*


object CurveCommand : CommandExecutor{
    private val plugin get() = NerdyStuff.instance
    private val title = "${ChatColor.GRAY}[${ChatColor.GOLD}Curves${ChatColor.GRAY}]${ChatColor.RESET} "

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage("$title${ChatColor.DARK_RED}only a player can run this command")
            return true
        }
        if (args.isEmpty()) {
            help(sender)
            return true
        }
        if(args[0] == "help") return help(sender)
        if(args[0] == "list") return list(sender)
        if(args[0] == "enable") return enable(sender, args.copyOfRange(1, args.size))
        if(args[0] == "disable") return disable(sender)
        if(args.size == 1){
            sender.sendMessage("$title${ChatColor.DARK_RED}select what you want to do")
            return true
        }
        if (args[1] == "generate") return generate(sender, args[0], args.copyOfRange(2, args.size))
        if (args[1] == "reset") return reset(sender, args[0])
        if (args[1] == "get") return get(sender,args[0])
        if (args[1] == "put") return putHere(sender,args[0], args.copyOfRange(2, args.size))
        if (args[1] == "setColor" || args[1]=="setcolor") return color(sender,args[0], args.copyOfRange(2, args.size))
        if (args[1] == "undo") return undo(sender,args[0])
        if (args[1] == "tools") return tools(sender,args[0])
        if (args[1] == "recalculate" || args[1] == "reCalculate" || args[1] == "recalc") return reCalc(sender,args[0], args.copyOfRange(2, args.size))
        sender.sendMessage("$title${ChatColor.DARK_RED}${args[1]} is not a possible option")
        return true
    }
    private fun tools(p: Player, name: String): Boolean{
        if(!visibleForPlayers.contains(p))
            enable(p,arrayOf())
        if(!bezierCurves.containsKey(name))
            putHere(p,name,arrayOf())
        p.sendMessage("$title${ChatColor.DARK_GREEN}giving tools for $name")
        giveTools(p, name)
        return true;
    }

    private fun reCalc(p: Player, name: String,args: Array<out String> ):Boolean{
        if(args.isNotEmpty()){
            val spread : Int? = args[0].toIntOrNull()
            if(spread == null)
                reCalculate(name)
            else
                reCalculate(name, spread)
        }
        else reCalculate(name)
        p.sendMessage("$title${ChatColor.DARK_GREEN}$name has been re-calculated")
        return true
    }

    private fun help(p: Player): Boolean{
        p.sendMessage("${title} to create bezier curves")
        p.sendMessage("- ${ChatColor.YELLOW}help:${ChatColor.RESET} this... ")
        p.sendMessage("- ${ChatColor.YELLOW}enable:${ChatColor.RESET} turn on being able to see the curves ${ChatColor.GRAY}(curves, pivots, all)")
        p.sendMessage("- ${ChatColor.YELLOW}disable:${ChatColor.RESET} turn off being able to see the curves")
        p.sendMessage("- ${ChatColor.YELLOW}<name> * :${ChatColor.RESET} enter the name of a curve")
        p.sendMessage("  * ${ChatColor.YELLOW}put:${ChatColor.RESET} add a pivot location to the curve ${ChatColor.GRAY}(standing location)( use target for target block location)")
        p.sendMessage("  * ${ChatColor.YELLOW}setColor:${ChatColor.RESET} change the color of the curve ${ChatColor.GRAY}(only if you have show turned on)(rgb value)")
        p.sendMessage("  * ${ChatColor.YELLOW}get:${ChatColor.RESET} get the information of the curve")
        p.sendMessage("  * ${ChatColor.YELLOW}generate:${ChatColor.RESET} generates the curve in to block ${ChatColor.GRAY}(also deletes the curves information)")
        p.sendMessage("  * ${ChatColor.YELLOW}undo:${ChatColor.RESET} undo the last position you added")
        p.sendMessage("  * ${ChatColor.YELLOW}reCalculate:${ChatColor.RESET} recalculates the curve ${ChatColor.GRAY}(put an extra number to determent the spread, default 8)")
        p.sendMessage("  * ${ChatColor.YELLOW}tools:${ChatColor.RESET} get tools for the current curve")

        return true;
    }

    private fun list(p: Player): Boolean{
        if(bezierCurves.isEmpty()){
            p.sendMessage("${title}there are no curves to list")
            return true
        }
        bezierCurves.keys.forEach(){key->
            p.sendMessage(key)
        }
        return true;
    }
    private fun disable(p: Player): Boolean{
        if(visibleForPlayers.contains(p)) {
            visibleForPlayers.remove(p)
            p.sendMessage("$title${ChatColor.DARK_GREEN}you can't see curves anymore")
        }
        else{
            p.sendMessage("$title${ChatColor.DARK_RED}you already can't see lol")
        }
        return true;
    }

    private fun enable(p: Player, args: Array<out String>): Boolean{

        if(args.isEmpty()){
            visibleForPlayers[p] = Part.ALL
            p.sendMessage("$title${ChatColor.GREEN}you can see curves and pivots now")
            return true
        }
        try{
            val part = Part.valueOf(args[0].uppercase())
            visibleForPlayers[p] = part
            val text = if(part == Part.CURVE) "curves" else if(part == Part.PIVOT) "pivots" else "curves and pivots"
            p.sendMessage("$title${ChatColor.GREEN}you can see $text now")
        }catch (e: Exception){
            p.sendMessage("$title${ChatColor.DARK_RED}${args[0]} is not valid, chose between ${ChatColor.RED}all, curve, pivot")
        }
        return true;
    }

    private fun generate(p: Player, name :String ,args: Array<out String>) : Boolean {
        if(args.isEmpty()){
            val bool =  generatePath(name)
            if(bool) p.sendMessage("$title${ChatColor.GREEN}generated path for $name ${ChatColor.GRAY}(deleted path)")
            else p.sendMessage("$title${ChatColor.DARK_RED}generated path for $name not possible, curve doesn't exist")
            return true
        }
        try{
            val bool =  generatePath(name, Material.valueOf(args[0].uppercase()))
            if(bool) p.sendMessage("$title${ChatColor.GREEN}generated path for $name ${ChatColor.GRAY}(deleted path)")
            else p.sendMessage("$title${ChatColor.DARK_RED}generated path for $name not possible, curve doesn't exist")
        }catch (e: Exception){
            p.sendMessage("$title${ChatColor.DARK_RED}${args[0]}, is not a valid block")
        }
        return true
    }

    private fun reset(p: Player, name :String) : Boolean {
        val removed : BezierCurve? = bezierCurves.remove(name)
        if(removed == null)  p.sendMessage("$title${ChatColor.DARK_RED}no curve with the name: $name")
        else p.sendMessage("$title${ChatColor.GREEN}resetting $name")
        return true
    }
    private fun get(p: Player, name :String) : Boolean {
        bezierCurves.forEach(){ curve ->
            if(curve.key == name){
                if(!curve.value.toStrings().isEmpty()) p.sendMessage("${title} curve ${name}:")
                curve.value.toStrings().forEach(){msg->
                    p.sendMessage(msg)
                }
                if(curve.value.toStrings().isEmpty())
                    p.sendMessage("${title}currently no lines inside your curve")
                return true
            }
        }
        p.sendMessage("$title${ChatColor.DARK_RED}no curve with the name: $name")
        return true
    }

    private fun putHere(p: Player, name:String, args: Array<out String>) : Boolean{
        if(args.isEmpty()) {
            val loc = p.location
            val bool = putLoc(name, loc)
            if (bool) p.sendMessage("$title${ChatColor.DARK_GREEN}added ${loc.blockX} ${loc.blockY} ${loc.blockZ} to $name")
            else p.sendMessage("$title${ChatColor.DARK_GREEN}created the curve named $name")
            return true
        }
        if(args[0] == "t" || args[0] == "target"){
            return putTarget(p, name)
        }
        else{
            p.sendMessage("$title${ChatColor.DARK_RED}${args[0]} isn't valid option")
            return true
        }
    }

    private fun putTarget(p: Player, name :String) : Boolean {
        val ignore = setOf(Material.AIR, Material.WATER)
        val tarBlock = p.getTargetBlock(ignore, 6)
        if(ignore.contains(tarBlock.type)){
            p.sendMessage("$title${ChatColor.DARK_RED}cant be added, you aren't targeting any blocks")
            return true;
        }
        val loc = tarBlock.location
        val middleLoc = Location(loc.world, loc.x + 0.5, loc.y+ 0.5, loc.z + 0.5)
        val bool = putLoc(name, middleLoc)
        if(bool) p.sendMessage("$title${ChatColor.DARK_GREEN}added ${middleLoc.x} ${middleLoc.y} ${middleLoc.z} to $name")
        else p.sendMessage("$title{ChatColor.DARK_GREEN}created the curve named $name")
        return true
    }

    private fun color(p : Player, name : String, args: Array<out String> ) : Boolean{
        if(args.size < 4){
            p.sendMessage("$title${ChatColor.DARK_RED}that's not a valid color or part")
            return true
        }
        val part : Part = try{Part.valueOf(args[0].uppercase()) } catch (e: Exception){
            p.sendMessage("$title${ChatColor.DARK_RED}${args[0]} is not valid, chose between ${ChatColor.RED}all, curve, pivot")
            return true
        }
        val r : Int? = args[1].toIntOrNull()
        val g : Int? = args[2].toIntOrNull()
        val b : Int? = args[3].toIntOrNull()
        if(r == null || g == null || b == null) {
            p.sendMessage("$title${ChatColor.DARK_RED}that's not a valid color, use rgb 0-255")
            return true
        }
        if(!bezierCurves.containsKey(name)){
            p.sendMessage("$title${ChatColor.DARK_RED}curve $name doesn't exists")
            return true
        }
        p.sendMessage("$title${ChatColor.GREEN}setting $name's color to this")
        bezierCurves[name]?.setColor(r,g,b, part)
        return true;
    }

    private fun undo(p: Player, name :String) : Boolean{
        if(undoLast(name)) p.sendMessage("$title${ChatColor.DARK_GREEN} $name's last location has been undone")
        else p.sendMessage("$title${ChatColor.DARK_RED} $name cant be undone further")
        return true
    }
}


object CurveTabCompleter : TabCompleter {

    override fun onTabComplete(sender: CommandSender, command: Command, label: String, args: Array<out String>): List<String> {
        val list : MutableList<String> = mutableListOf<String>();

        if (sender !is Player || args.isEmpty())
            return list
        //val p : Player = sender
        if(args[0] == "") {
            list.add("enable")
            list.add("disable")
            list.add("help")
            list.add("list")
            list.add("<name of curve>")
            return list
        }
        if(args.size < 2 || args[0] == "disable" || args[0] == "help" || args[0] == "list" || args[0] == "<name") return list
        if(args[0] == "enable" &&  args[1] == ""){
            return Part.values().map { it.toString().lowercase()}
        }
        if (args[1] == "") {
            list.add("generate")
            list.add("reset")
            list.add("get")
            list.add("put")
            list.add("setColor")
            list.add("undo")
            list.add("reCalculate")
            list.add("tools")
            return list
        }
        if(args.size < 3) return list
        if(args[1] == "generate" && args[2] == ""){
            return Material.values().filter { it.isBlock }.map { it.toString().lowercase()}
        }
        if(args[1] == "color" &&  args[2] == ""){
            return Part.values().map { it.toString().lowercase()}
        }
        if(args[1] == "put" &&  args[2] == ""){
            list.add("target")
            return list
        }


        return list;
    }
}