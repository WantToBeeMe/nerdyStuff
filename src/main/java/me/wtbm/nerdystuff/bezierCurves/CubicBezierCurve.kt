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

    fun pointAtT(t: Double) : Point{ //P(t)
        val lx = (-t*t*t +3*t*t -3*t + 1)*points[0].x + //p1
                (3*t*t*t -6*t*t +3*t)*points[1].x + //p2
                (-3*t*t*t +3*t*t)*points[2].x + //p3
                t*t*t*points[3].x //p4
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
    fun velocityAtT(t: Double):Point{ //P'(t)
        val vx = (-3*t*t +6*t -3)*points[0].x + //p1
                (9*t*t -12*t +3)*points[1].x + //p2
                (-9*t*t +6*t)*points[2].x + //p3
                3*t*t*points[3].x //p4
        val vy = (-3*t*t +6*t -3)*points[0].y +
                (9*t*t -12*t +3)*points[1].y +
                (-9*t*t +6*t)*points[2].y +
                3*t*t*points[3].y
        val vz =(-3*t*t +6*t -3)*points[0].z +
                (9*t*t -12*t +3)*points[1].z +
                (-9*t*t +6*t)*points[2].z +
                3*t*t*points[3].z
        val vr =((-3*t*t +6*t -3)*points[0].rotation +
                (9*t*t -12*t +3)*points[1].rotation +
                (-9*t*t +6*t)*points[2].rotation +
                3*t*t*points[3].rotation).toInt()
        return Point(vx,vy,vz,PointTypes.VELOCITY,vr)
    }
    fun accelerationAtT(t: Double):Point{ //P''(t)
        val ax = (-6*t +6)*points[0].x + //p1
                (18*t -12)*points[1].x + //p2
                (-18*t +6)*points[2].x + //p3
                6*t*points[3].x //p4
        val ay = (-6*t +6)*points[0].y +
                (18*t -12)*points[1].y +
                (-18*t +6)*points[2].y+
                6*t*points[3].y
        val az = (-6*t +6)*points[0].z +
                (18*t -12)*points[1].z +
                (-18*t +6)*points[2].z+
                6*t*points[3].z
        val ar =( (-6*t +6)*points[0].rotation +
                (18*t -12)*points[1].rotation +
                (-18*t +6)*points[2].rotation +
                6*t*points[3].rotation).toInt()
        return Point(ax,ay,az,PointTypes.ACCELERATION,ar)
    }
    fun getJolt(): Point{ //P'''(t)   also no idea when you would ever need this lol
        val jx =(-6)*points[0].x + //p1
                (18)*points[1].x + //p2
                (-18)*points[2].x + //p3
                (6)*points[3].x //p4
        val jy =(-6)*points[0].y +
                (18)*points[1].y +
                (-18)*points[2].y+
                (6)*points[3].y
        val jz =(-6)*points[0].z +
                (18)*points[1].z +
                (-18)*points[2].z+
                (6)*points[3].z
        val jr =((-6)*points[0].rotation +
                (18)*points[1].rotation +
                (-18)*points[2].rotation+
                (6)*points[3].rotation)
        return Point(jx,jy,jz,PointTypes.JOLT,jr)
    }
}