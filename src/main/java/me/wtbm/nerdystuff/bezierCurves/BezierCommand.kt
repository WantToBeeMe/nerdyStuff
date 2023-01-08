package me.wtbm.nerdystuff.bezierCurves

import me.wtbm.nerdystuff.NerdyStuff
import me.wtbm.nerdystuff.toString
import me.wtbm.nerdystuff.bezierCurves.BezierSplineController.createNewSpline
import me.wtbm.nerdystuff.bezierCurves.BezierSplineController.getSplines
import me.wtbm.nerdystuff.bezierCurves.BezierSplineController.tryDeleteSpline
import me.wtbm.nerdystuff.bezierCurves.BezierTools.giveTools
import me.wtbm.nerdystuff.old_bezier.OldBezierCurve
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player
import java.util.HashMap


object BezierCommand : CommandExecutor{
    private val plugin get() = NerdyStuff.instance
    val title = "${ChatColor.GRAY}[${ChatColor.GOLD}Bezier${ChatColor.GRAY}]${ChatColor.RESET}"
    private val r = "${ChatColor.DARK_RED}" //red
    private val hr : (String)-> String = {h -> "${ChatColor.RED}$h${ChatColor.DARK_RED}"} //highlight red
    private val g = "${ChatColor.DARK_GREEN}" //green
    private val hg : (String)-> String = {h -> "${ChatColor.GREEN}$h${ChatColor.DARK_GREEN}"} //highlight green
    private val help : (String)-> String = {h -> "${ChatColor.YELLOW}$h${ChatColor.WHITE}"} //new help keyword
    private val lg  = "${ChatColor.GRAY}" //light gray

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
        if(args[0] == "new") return new(sender, args.copyOfRange(1, args.size))
        if(args[0] == "list") return list(sender, args.copyOfRange(1, args.size))
        if(args[0] == "delete") return delete(sender, args.copyOfRange(1, args.size))
        if(args[0] == "tools") return tools(sender, args.copyOfRange(1,args.size))


        return true;
    }

    private fun tools(p: Player, args: Array<out String>) : Boolean {
        if(args.size != 1){
            p.sendMessage("$title$r specify which one you want to get tools for ${hr("/bezier tools <name>")}")
            return true
        }
        if(getSplines().containsKey(args[0])){
            giveTools(p, args[0])
            p.sendMessage("$title$g tools for ${hg(args[0])} have been given")
            return true
        }
        else{
            p.sendMessage("$title ${hr(args[0])} doesn't exist, therefore tools can't be given")
            return true
        }
    }

    private fun delete(p: Player, args: Array<out String>) : Boolean {
        if(args.size != 1) {
            p.sendMessage("$title$r specify which one you want to do delete ${hr("/bezier delete <name>")}")
            return true
        }
        if(tryDeleteSpline(args[0])){
            p.sendMessage("$title ${hg(args[0])} has been deleted")
            return true
        }
        else{
            p.sendMessage("$title ${hr(args[0])} doesn't exist, therefore cant be deleted")
            return true
        }

    }

    private fun new(p: Player, args: Array<out String>) : Boolean{
        if(args.size != 2){
            p.sendMessage("$title$r select what you want to do ${hr("/bezier new [curve/spline] <name>")}")
            return true
        }
        if(args[0].lowercase() == "curve"){
            p.sendMessage("$title$g cool, not implemented yet")
            return true
        }
        else if(args[0].lowercase() == "spline"){
            //check if the name already exist in de curves here (if it's implemented at least)
            val bool = createNewSpline(p.location, args[1])
            giveTools(p, args[1])
            if(!bool) p.sendMessage("$title$r spline with the name ${hr(args[1])} already exists")
            else p.sendMessage("$title$g created a new spline with the name ${hg(args[1])} ")
            return true
        }
        else {
            p.sendMessage("$title${hr(args[0])}$r is not a valid type, use ${hr("curve")} or ${hr("spline")} ")
            return true
        }

    }

    private fun list(p: Player , args: Array<out String>) : Boolean{
        if(args.size != 1){
            p.sendMessage("$title$r select what you want to do ${hr("/list [curve/spline]")}")
            return true
        }
        else if(args[0] == "curve"){
            p.sendMessage("$title$g cool, not implemented yet")
            return true
        }
        else if(args[0] == "spline"){
            val splines = getSplines()
            p.sendMessage("${title} ${help(splines.size.toString())} total of splines")
            splines.forEach(){spline->
                p.sendMessage("${help(spline.key+":")} ${spline.value.firstLoc().toString(2)} ${help("->")} ${spline.value.lastLoc().toString(2)} ")
            }
            return true
        }
        else{
            p.sendMessage("$title$r select what you want to do ${hr("/list [curve/spline]")}")
            return true
        }
    }
    private fun help(p: Player): Boolean{
        p.sendMessage("${title} /bezier or /b to create bezier curves")
        p.sendMessage("- ${help("help:")} this... ")
        p.sendMessage("- ${help("new")} create new curve or spline $lg /bezier new [curve/spline] <name> ")
        p.sendMessage("- ${help("list")} list all the current existing curves/splines $lg /bezier list [curve/spline]")
        p.sendMessage("- ${help("build")} builds the curve/spline $lg /bezier build <name> ")
        p.sendMessage("- ${help("delete")} deletes the curve/spline $lg /bezier delete <name> ")
        p.sendMessage("- ${help("tools")} regain the editing tools for the given curve/spline $lg /bezier tools <name> ")
        return true;
    }

}


object BezierTabCompleter : TabCompleter {

    override fun onTabComplete(sender: CommandSender, command: Command, label: String, args: Array<out String>): List<String> {
        val list : MutableList<String> = mutableListOf<String>();

        if (sender !is Player || args.isEmpty())
            return list

        val keyWords: MutableMap<String, List<String>> = hashMapOf(
            Pair("help", listOf("0")),
            Pair("new", listOf("0")),
            Pair("delete",listOf("0")),
            Pair("build", listOf("0")),
            Pair("tools", listOf("0")),
            Pair("list", listOf("0")),
            Pair("curve", listOf("new", "list")),
            Pair("spline", listOf("new", "list")),
        )
        getSplines().forEach(){spline->
            keyWords[spline.key] = listOf("delete", "build", "tools")
        }

        val size = args.size
        val lastArg = args.last()
        val  secondLastArg = if(size >= 2) args[size-2] else null
        //val p : Player = sender
        keyWords.forEach() pairs@ {pair ->
            pair.value.forEach constrains@ {cons->
                val maby = cons.toIntOrNull()
                if(maby != null){
                    if(maby == size-1 && pair.key.startsWith(lastArg)){
                        list.add(pair.key)
                        return@pairs
                    }
                }
                else if(secondLastArg != null){
                    if(cons == secondLastArg && pair.key.startsWith(lastArg)){
                        list.add(pair.key)
                        return@pairs
                    }
                }
            }
        }

        return list;
    }
}