package me.wtbm.nerdystuff

import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player


interface BetterTabCompleter : TabCompleter {
    //first: argument suggestion to be added to the TabComplete
    //second: PLACE AFTER/AT - list of places where the argument will be suggested, an int index to be placed at, or an argument to be placed after
    //third: PLACE IF ALSO - list of extra arguments that need to be somewhere in the args next to the 'second' args explained above. (or not if you put a ! as a prefix)
    //example:  Triple("argument", listOf("0","here","!notHere"),listOf("everywhere","!nowhere"))
    val keyWords: MutableList<Triple<String, Array<String>, Array<String>>>
    fun needsConstantUpdate()

    override fun onTabComplete(sender: CommandSender, command: Command, label: String, args: Array<out String>): List<String> {
        needsConstantUpdate()
        val list : MutableList<String> = mutableListOf<String>();

        if (sender !is Player || args.isEmpty())
            return list
        val args = (args.map { it.lowercase() }).toTypedArray()

        val size = args.size
        val lastArg = args.last()
        val  secondLastArg = if(size >= 2) args[size-2] else null
        //val p : Player = sender
        keyWords.forEach() triple@ {trip ->
            trip.third.forEach(){must->
                if(must.startsWith("!")){
                    if(args.contains(must.removePrefix("!").lowercase())) {
                        return@triple
                    }
                }
                else {
                    if (!args.contains(must.lowercase())) {
                        return@triple
                    }
                }
            }
            trip.second.forEach constrains@ {cons->
                val maby = cons.toIntOrNull()
                if(maby != null){
                    if(maby == size-1 && trip.first.lowercase().startsWith(lastArg)){
                        list.add(trip.first)
                        return@triple
                    }
                }
                else if(secondLastArg != null){
                    if(cons == secondLastArg && trip.first.lowercase().startsWith(lastArg)){
                        list.add(trip.first)
                        return@triple
                    }
                }
            }
        }
        return list;
    }
}