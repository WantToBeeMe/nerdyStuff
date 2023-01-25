package me.wtbm.nerdystuff

import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.util.Vector
import kotlin.math.sqrt

fun Location.toString(decimal : Int): String {
    val dev = Math.pow(10.0, decimal-1.0)
    val startX:Double = Math.round(this.x * dev) / dev
    val startY:Double = Math.round(this.y * dev) / dev
    val startZ:Double = Math.round(this.z * dev) / dev
    return "$startX $startY $startZ"
}

fun Player.lookingAtPoint(p:Location, maxDistance : Double = 10.0, offsetMargin : Double = 0.5) : Boolean {
    if(this.eyeLocation.distance(p) > maxDistance) return false
    val point = p.toVector()
    val rc =  this.eyeLocation.direction
    val devide = (rc.x * rc.x + rc.y * rc.y + rc.z * rc.z)
    val base = (point.x * rc.x + point.y * rc.y + point.z * rc.z)-(this.eyeLocation.x * rc.x + this.eyeLocation.y * rc.y + this.eyeLocation.z * rc.z)
    val t = base/devide
    val distance = if(t > 0) point.distance(this.eyeLocation.toVector().add(Vector(t*rc.x, t*rc.y, t*rc.z))) else return false
    return distance < offsetMargin
}

fun abcFormula(a:Double,b:Double,c:Double):Pair<Double,Double>?{
    val d = (b*b)-(4*a*c)
    if(d < 0 || a == 0.0) return null
    val r = sqrt(d)
    val t1 = (-b + r )/(2*a)
    val t2 = (-b - r )/(2*a)
    return Pair(t1,t2)
}