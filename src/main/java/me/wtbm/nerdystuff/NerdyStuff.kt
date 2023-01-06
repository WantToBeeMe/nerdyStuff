package me.wtbm.nerdystuff

import me.wtbm.nerdystuff.bezierCurves.BezierCommand
import me.wtbm.nerdystuff.bezierCurves.BezierTabCompleter
import me.wtbm.nerdystuff.old_bezier.OldBezierCurveController.showCurves
import me.wtbm.nerdystuff.old_bezier.OldBezierListener
import me.wtbm.nerdystuff.old_bezier.OldCurveCommand
import me.wtbm.nerdystuff.old_bezier.OldCurveTabCompleter
import me.wtbm.nerdystuff.old_bezier.OldBezierToolsController.toolsTick
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.plugin.java.annotation.command.Command
import org.bukkit.plugin.java.annotation.command.Commands
import org.bukkit.plugin.java.annotation.dependency.Library
import org.bukkit.plugin.java.annotation.plugin.ApiVersion
import org.bukkit.plugin.java.annotation.plugin.Description
import org.bukkit.plugin.java.annotation.plugin.Plugin
import org.bukkit.plugin.java.annotation.plugin.author.Author


@Plugin(name = "NerdyStuff", version ="1.0")
@ApiVersion(ApiVersion.Target.v1_19)
@Author("WantToBeeMe")
@Description("you know that subject no one liked at school, yea, that (math lol)")

@Commands(
    Command(name = "oldCurve", aliases = ["oc"], usage = "/oldCurve help"),
    Command(name = "bezier", aliases = ["b"], usage = "/bezier help")
)

@Library("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.7.22") //kotlin !!
class NerdyStuff : JavaPlugin() {

    companion object {
        lateinit var instance: NerdyStuff
    }

    override fun onEnable() {
        instance = this
        getCommand("oldCurve")?.setExecutor(OldCurveCommand)
        getCommand("oldCurve")?.tabCompleter = OldCurveTabCompleter

        getCommand("bezier")?.setExecutor(BezierCommand)
        getCommand("bezier")?.tabCompleter = BezierTabCompleter

        getServer().getPluginManager().registerEvents(OldBezierListener, this)

        server.scheduler.scheduleSyncRepeatingTask(instance, { showCurves() }, 0, 8)
        server.scheduler.scheduleSyncRepeatingTask(instance, { toolsTick() }, 20, 20)
    }

    override fun onDisable() {}

}