package me.wtbm.nerdystuff.bezierCurves

import me.wtbm.nerdystuff.NerdyStuff
import org.bukkit.Location
import kotlin.math.sqrt

class Point(var x: Double, var y: Double, var z:Double, var type : PointTypes = PointTypes.CONTROL , var rotation : Int = 0) {

    //lerp == linear interpolation
    fun lerpFrom(p:Point, t:Double) : Point{
        val lx = (1-t)*p.x + t*x
        val ly = (1-t)*p.y + t*y
        val lz = (1-t)*p.z + t*z
        val lr = ((1-t)*p.rotation + t*rotation).toInt()
        return Point(lx,ly,lz,PointTypes.CONTROL,lr)
    }

    fun lerpTo(p:Point, t:Double) : Point{
        val lx = (1-t)*x + t*p.x
        val ly = (1-t)*y + t*p.y
        val lz = (1-t)*z + t*p.z
        val lr = ((1-t)*rotation + t*p.rotation).toInt()
        return Point(lx,ly,lz,PointTypes.CONTROL,lr)
    }

    fun distanceTo(p:Point) : Double {
        return sqrt((x - p.x)*(x - p.x) + (y - p.y)*(y - p.y) + (z - p.z)*(z - p.z))
    }


    fun changeLocation(loc: Location){
        x = loc.x
        y = loc.y
        z = loc.z
    }

    companion object {
        fun newPoint(loc : Location, type : PointTypes = PointTypes.CONTROL, rotation : Int = 0) : Point{
            return Point(loc.x, loc.y, loc.z, type, rotation)
        }
    }

}