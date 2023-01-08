package me.wtbm.nerdystuff

import org.bukkit.Location

fun Location.toString(decimal : Int): String {
    val dev = Math.pow(10.0, decimal-1.0)
    val startX:Double = Math.round(this.x * dev) / dev
    val startY:Double = Math.round(this.y * dev) / dev
    val startZ:Double = Math.round(this.z * dev) / dev
    return "$startX $startY $startZ"
}