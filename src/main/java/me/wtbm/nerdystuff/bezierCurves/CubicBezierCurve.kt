package me.wtbm.nerdystuff.bezierCurves

import org.bukkit.Location
import org.bukkit.World

class CubicBezierCurve(loc : Location) {
    var points: Array<Point> = arrayOf(
        Point.newPoint(loc, PointTypes.ANCHOR),
        Point.newPoint(loc),
        Point.newPoint(loc),
        Point.newPoint(loc)
    )
    val world : World? = loc.world

    fun locationAtT(t: Double) : Location{
        val point = pointAtT(t)
        return Location(world, point.x, point.y, point.z )
    }
    fun pointAtT(t: Double) : Point{
        val lx = (-t*t*t +3*t*t -3*t + 1)*points[0].x +
                (3*t*t*t -6*t*t +3*t)*points[1].x +
                (-3*t*t*t +3*t*t)*points[2].x +
                t*t*t*points[3].x
        val ly = (-t*t*t +3*t*t -3*t + 1)*points[0].y +
                (3*t*t*t -6*t*t +3*t)*points[1].y +
                (-3*t*t*t +3*t*t)*points[2].y +
                t*t*t*points[3].y
        val lz = (-t*t*t +3*t*t -3*t + 1)*points[0].z +
                (3*t*t*t -6*t*t +3*t)*points[1].z +
                (-3*t*t*t +3*t*t)*points[2].z +
                t*t*t*points[3].z
        val lr =((-t*t*t +3*t*t -3*t + 1)*points[0].rotation +
                (3*t*t*t -6*t*t +3*t)*points[1].rotation +
                (-3*t*t*t +3*t*t)*points[2].rotation +
                t*t*t*points[3].rotation).toInt()
        return Point(lx,ly,lz,PointTypes.CONTROL,lr)
    }
}