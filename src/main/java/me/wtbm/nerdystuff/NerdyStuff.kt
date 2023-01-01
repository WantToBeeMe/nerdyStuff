package me.wtbm.nerdystuff

import me.wtbm.nerdystuff.curves.BezierCurve
import me.wtbm.nerdystuff.curves.BezierCurveController.showCurves
import me.wtbm.nerdystuff.curves.CurveCommand
import me.wtbm.nerdystuff.curves.CurveListener
import me.wtbm.nerdystuff.curves.CurveTabCompleter
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.plugin.java.annotation.command.Command
import org.bukkit.plugin.java.annotation.command.Commands
import org.bukkit.plugin.java.annotation.dependency.Library
import org.bukkit.plugin.java.annotation.plugin.ApiVersion
import org.bukkit.plugin.java.annotation.plugin.Description
import org.bukkit.plugin.java.annotation.plugin.Plugin
import org.bukkit.plugin.java.annotation.plugin.author.Author
import java.util.*

@Plugin(name = "NerdyStuff", version ="1.0")
@ApiVersion(ApiVersion.Target.v1_19)
@Author("WantToBeeMe")
@Description("you know that subject no one liked at school, yea, that (math lol)")

@Commands(
    Command(name = "curve", aliases = ["c"], usage = "/curve help")
)

@Library("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.7.22") //kotlin !!
class NerdyStuff : JavaPlugin() {

    companion object {
        lateinit var instance: NerdyStuff
    }

    override fun onEnable() {
        instance = this
        getCommand("curve")?.setExecutor(CurveCommand)
        getCommand("curve")?.tabCompleter = CurveTabCompleter

        getServer().getPluginManager().registerEvents(CurveListener, this)

        val id = server.scheduler.scheduleSyncRepeatingTask(instance, { showCurves() }, 0, 8)

    }

    override fun onDisable() {}

}