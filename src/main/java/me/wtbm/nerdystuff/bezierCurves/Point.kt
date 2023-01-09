package me.wtbm.nerdystuff.bezierCurves

import org.bukkit.Location
import org.bukkit.World
import kotlin.math.sqrt

class Point(var x: Double, var y: Double, var z:Double, var type : PointTypes = PointTypes.CONTROL , var rotation : Int = 0) {



    operator fun plus(p: Point) : Point = Point(x+p.x, y+p.y, z+p.z , PointTypes.CONTROL, rotation+p.rotation)
    //operator fun minus(p: Point) : Point = Point(x-p.x, y-p.y, z-p.z , PointTypes.CONTROL, rotation-p.rotation)
    //operator fun times(p: Point) : Point = Point(x*p.x, y*p.y, z*p.z , PointTypes.CONTROL, rotation*p.rotation)
    //operator fun div(p: Point) : Point = Point(x/p.x, y/p.y, z/p.z , PointTypes.CONTROL, rotation/p.rotation)

    //operator fun plus(d: Double) : Point = Point(x+d, y+d, z+d , PointTypes.CONTROL, rotation+d.toInt())
    //operator fun minus(d: Double) : Point = Point(x-d, y-d, z-d , PointTypes.CONTROL, rotation-d.toInt())
    operator fun times(d: Double) : Point = Point(x*d, y*d, z*d , PointTypes.CONTROL, rotation*d.toInt())
    //operator fun div(d: Double) : Point = Point(x/d, y/d, z/d , PointTypes.CONTROL, rotation/d.toInt())


    fun distanceTo(p:Point) : Double = sqrt((x - p.x)*(x - p.x) + (y - p.y)*(y - p.y) + (z - p.z)*(z - p.z))
    fun getMirrorFrom(p:Point) : Point = Point(this.x+(this.x-p.x),this.y+(this.y-p.y),this.z+(this.z-p.z),PointTypes.CONTROL)
    fun toLocation(world : World?) : Location = Location(world, x, y, z)

    fun getAlignedFrom(p:Point) : Point{
        val multiply : Double = 0.01+((0..20).random().toDouble()/10)
        return Point(this.x+(this.x-p.x)*multiply,this.y+(this.y-p.y)*multiply,this.z+(this.z-p.z)*multiply,PointTypes.CONTROL)
    }

    fun makeInt(){
        x = x.toInt() + 0.0
        y = y.toInt() + 0.0
        z = z.toInt() + 0.0
    }
    /*
    fun changeLocation(loc: Location){
        x = loc.x
        y = loc.y
        z = loc.z
    }
     */

    companion object {
        fun newPoint(loc : Location, type : PointTypes = PointTypes.CONTROL, rotation : Int = 0) : Point = Point(loc.x, loc.y, loc.z, type, rotation)
    }
    //lerp == linear interpolation
    fun lerpTo(p:Point, t:Double) : Point{
        val lx = (1-t)*x + t*p.x
        val ly = (1-t)*y + t*p.y
        val lz = (1-t)*z + t*p.z
        val lr = ((1-t)*rotation + t*p.rotation).toInt()
        return Point(lx,ly,lz,PointTypes.CONTROL,lr)
    }
}