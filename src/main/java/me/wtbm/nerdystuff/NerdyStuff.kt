package me.wtbm.nerdystuff

import me.wtbm.nerdystuff.bezier.BezierCurveController.showCurves
import me.wtbm.nerdystuff.bezier.BezierListener
import me.wtbm.nerdystuff.bezier.CurveCommand
import me.wtbm.nerdystuff.bezier.CurveTabCompleter
import me.wtbm.nerdystuff.bezier.BezierToolsController.toolsTick
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.plugin.java.annotation.command.Command
import org.bukkit.plugin.java.annotation.command.Commands
import org.bukkit.plugin.java.annotation.dependency.Library
import org.bukkit.plugin.java.annotation.plugin.ApiVersion
import org.bukkit.plugin.java.annotation.plugin.Description
import org.bukkit.plugin.java.annotation.plugin.Plugin
import org.bukkit.plugin.java.annotation.plugin.author.Author
import org.bukkit.scheduler.BukkitRunnable


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

        getServer().getPluginManager().registerEvents(BezierListener, this)

        server.scheduler.scheduleSyncRepeatingTask(instance, { showCurves() }, 0, 8)
        server.scheduler.scheduleSyncRepeatingTask(instance, { toolsTick() }, 20, 20)
    }

    override fun onDisable() {}

}