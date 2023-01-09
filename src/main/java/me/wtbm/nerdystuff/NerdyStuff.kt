package me.wtbm.nerdystuff

import me.wtbm.nerdystuff.bezierCurves.BezierCommand
import me.wtbm.nerdystuff.bezierCurves.BezierSplineController
import me.wtbm.nerdystuff.bezierCurves.BezierSplineController.showBezier
import me.wtbm.nerdystuff.bezierCurves.BezierTabCompleter
import me.wtbm.nerdystuff.bezierCurves.BezierToolsListener
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
    Command(name = "bezier", aliases = ["b"], usage = "/bezier help")
)

@Library("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.7.22") //kotlin !!
class NerdyStuff : JavaPlugin() {

    companion object {
        lateinit var instance: NerdyStuff
    }

    override fun onEnable() {
        instance = this

        getCommand("bezier")?.setExecutor(BezierCommand)
        getCommand("bezier")?.tabCompleter = BezierTabCompleter

        getServer().getPluginManager().registerEvents(BezierToolsListener, this)

        server.scheduler.scheduleSyncRepeatingTask(instance, { BezierSplineController.tick01() }, 0, 1)
        server.scheduler.scheduleSyncRepeatingTask(instance, { BezierSplineController.tick10() }, 0, 10)
    }

    override fun onDisable() {}

}

