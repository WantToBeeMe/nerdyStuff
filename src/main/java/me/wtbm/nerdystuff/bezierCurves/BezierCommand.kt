package me.wtbm.nerdystuff.bezierCurves

import me.wtbm.nerdystuff.NerdyStuff
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
    private val title = "${ChatColor.GRAY}[${ChatColor.GOLD}Bezier${ChatColor.GRAY}]${ChatColor.RESET}"
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

        return true;
    }

    private fun help(p: Player): Boolean{
        p.sendMessage("${title} /bezier or /b to create bezier curves")
        p.sendMessage("- ${help("help:")} this... ")
        p.sendMessage("- ${help("new")} create new curve or spline $lg /bezier new [curve/spline] <name> ")
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

        var keyWords: MutableMap<String, List<String>> = hashMapOf(
            Pair("help", listOf("0")),
            Pair("new", listOf("0")),
            Pair("delete",listOf("0")),
            Pair("build", listOf("0")),
            Pair("tools", listOf("0")),
            Pair("curve", listOf("new")),
            Pair("spline", listOf("new"))
        )
        //list.forEach(){
        //    keyWords.put(it,listOf("curve", "spline", "delete", "build", "tools")
        //}
        val size = args.size
        val lastArg = args.last()
        val  secondLastArg = if(size >= 2) args[size-2] else null
        //val p : Player = sender
        keyWords.forEach() pairs@ {pair ->
            pair.value.forEach constrains@ {cons->
                val maby = cons.toIntOrNull()
                if(maby != null){
                    if(maby == size-1 && pair.key.startsWith(lastArg)){
                        list.add(pair.key.removePrefix(lastArg))
                        return@pairs
                    }
                }
                else if(secondLastArg != null){
                    if(cons == secondLastArg && pair.key.startsWith(lastArg)){
                        list.add(pair.key.removePrefix(lastArg))
                        return@pairs
                    }
                }
            }
        }

        return list;
    }
}