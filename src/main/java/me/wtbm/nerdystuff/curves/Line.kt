package me.wtbm.nerdystuff.curves

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.World

class Line(RCx: Double, Sx: Double, RCy: Double, Sy: Double, RCz: Double, Sz: Double, val world: World?) {
    val linearX : (Double) -> (Double) = { T -> RCx*T + Sx }
    val linearY : (Double) -> (Double) = { T -> RCy*T + Sy }
    val linearZ : (Double) -> (Double) = { T -> RCz*T + Sz }

    fun getLocation(T: Double) : Location {
        if(T <  0 || T > 1) Bukkit.getLogger().info("A line went out of bounds")
        return Location(world,linearX(T), linearY(T), linearZ(T))
    }

    override fun toString() : String{
        val start = getLocation(0.0)
        val end = getLocation(1.0)
        val startX:Double = Math.round(start.x * 10.0) / 10.0
        val startY:Double = Math.round(start.y * 10.0) / 10.0
        val startZ:Double = Math.round(start.z * 10.0) / 10.0
        val endX:Double = Math.round(end.x * 10.0) / 10.0
        val endY:Double = Math.round(end.y * 10.0) / 10.0
        val endZ:Double = Math.round(end.z * 10.0) / 10.0
        return "$startX $startY $startZ ${ChatColor.YELLOW}->${ChatColor.RESET} $endX $endY $endZ "
    }

    companion object {
        fun new(startLoc: Location, endLoc : Location) : Line{
            val diffX = endLoc.x - startLoc.x
            val diffY = endLoc.y - startLoc.y
            val diffZ = endLoc.z - startLoc.z
            return Line(diffX, startLoc.x, diffY, startLoc.y, diffZ, startLoc.z, startLoc.world)
        }
    }

}