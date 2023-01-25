package me.wtbm.nerdystuff.bezierCurves

import me.wtbm.nerdystuff.BetterTabCompleter
import me.wtbm.nerdystuff.NerdyStuff
import me.wtbm.nerdystuff.bezierCurves.BezierSplineController.buildSpline
import me.wtbm.nerdystuff.toString
import me.wtbm.nerdystuff.bezierCurves.BezierSplineController.createNewSpline
import me.wtbm.nerdystuff.bezierCurves.BezierSplineController.getSplines
import me.wtbm.nerdystuff.bezierCurves.BezierSplineController.makeSplineInt
import me.wtbm.nerdystuff.bezierCurves.BezierSplineController.startAnimation
import me.wtbm.nerdystuff.bezierCurves.BezierSplineController.tryDeleteSpline
import me.wtbm.nerdystuff.bezierCurves.BezierTools.giveTools
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player


object BezierCommand : CommandExecutor{
    private val notAllowed : Array<String> = arrayOf("0","1","2","3","4","5","6","7","8","9","[","]","{","}","!",".","?",":",";",",") // characters that are not allowed in a name
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
        val args = (args.map { it.lowercase() }).toTypedArray()
        if(args[0] == "help") return help(sender)
        if(args[0] == "list") return list(sender, args.copyOfRange(1, args.size))
        if(args[0] == "new") return new(sender, args.copyOfRange(1, args.size))
        if(args[0] == "delete") return delete(sender, args.copyOfRange(1, args.size))
        if(args[0] == "tools") return tools(sender, args.copyOfRange(1,args.size))
        if(args[0] == "options") return options(sender, args.copyOfRange(1,args.size))
        if(args[0] == "build") return build(sender, args.copyOfRange(1,args.size))
        return true;
    }

    private fun build(p: Player, args: Array<out String>) : Boolean{
        if(args.size < 2){
            p.sendMessage("$title$r specify which one you want to get tools for ${hr("/bezier build <name> <block>")}")
            return true
        }

        if(getSplines().containsKey(args[0])){
           try{
               val mat = Material.valueOf(args[1].uppercase())
               if(buildSpline(args[0], mat.createBlockData() )){
                   p.sendMessage("$title ${hg(args[0])} spline has been build with ${hg(args[1])}")
                   p.sendMessage("$title ${hr(args[0])} note that this cant be undone, spline has been deleted from the list")}

                else p.sendMessage("$title ${hr(args[0])} something went wrong")
               return true
            } catch (e: Exception) {
               p.sendMessage("$title ${hr(args[0])} select the setting you want to change")
               return true
           }
        }
        else{
            p.sendMessage("$title ${hr(args[0])} doesn't exist, therefore options cant be changed")
            return true
        }
    }

    private fun options(p: Player, args: Array<out String>) : Boolean{
        if(args.size < 2){
            p.sendMessage("$title$r specify which one you want to get tools for ${hr("/bezier options <name> <option>")}")
            return true
        }

        if(getSplines().containsKey(args[0])){
            if(args[1] == "makeint" ){
                makeSplineInt(args[0])
                p.sendMessage("$title ${hg(args[0])} all control points have been changed to its int equivalent")
                p.sendMessage("$title$r note that this cant be undone, but you can move them to a non int value manually ")
                p.sendMessage("$title$r this has an almost 100% chance of breaking every mirror/aligned continuity a tiny bit (not a lot though) ")
                return true
            }
            else if(args[1] == "animate"){
                val bool = startAnimation(args[0])
                if(bool) p.sendMessage("$title$g animation started for ${hg(args[0])}")
                else p.sendMessage("$title ${hr(args[0])} doesn't exist")
                return true
            }
            p.sendMessage("$title ${hr(args[0])} select the setting you want to change")
            return true
        }
        else{
            p.sendMessage("$title ${hr(args[0])} doesn't exist, therefore options cant be changed")
            return true
        }
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
        if(args[0] == "curve"){
            p.sendMessage("$title$g cool, not implemented yet")
            return true
        }
        else if(args[0] == "spline"){
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


object BezierTabCompleter : BetterTabCompleter {

    override val keyWords: MutableList<Triple<String, Array<String>, Array<String>>> = mutableListOf(
        Triple("help", arrayOf("0"),emptyArray()),
        Triple("new", arrayOf("0"),emptyArray()),
        Triple("delete",arrayOf("0"),emptyArray()),
        Triple("build", arrayOf("0"),emptyArray()),
        Triple("tools", arrayOf("0"),emptyArray()),
        Triple("list", arrayOf("0"), emptyArray()),
        Triple("curve", arrayOf("new", "list"),emptyArray()),
        Triple("spline", arrayOf("new", "list"),emptyArray()),
        Triple("options", arrayOf("0"), emptyArray()),
    )

    var oldSplines = getSplines().keys.toTypedArray()
    override fun needsConstantUpdate() {
        val splines = getSplines().keys.toTypedArray()

        keyWords.removeIf { trip -> !splines.contains(trip.first) && oldSplines.contains(trip.first) }
        oldSplines = splines;

        keyWords.add(Triple("makeInt", splines , arrayOf("options") ))
        keyWords.add(Triple("animate", splines , arrayOf("options") ))

        splines.forEach(){spline-> keyWords.add(Triple(spline,  arrayOf("delete", "build", "tools", "options"), emptyArray())) }
        Material.values().forEach { mat-> keyWords.add(Triple(mat.toString().lowercase(), splines, arrayOf("build"))) }
    }

}