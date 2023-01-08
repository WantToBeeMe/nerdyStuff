package me.wtbm.nerdystuff.bezierCurves

import org.bukkit.Location
import org.bukkit.World

class CubicBezierCurve(ancLoc : Location,
                       conLoc1 : Location = ancLoc.clone().add((-10..10).random().toDouble(),(-10..10).random().toDouble(),(-10..10).random().toDouble()),
                       knotLoc : Location = ancLoc.clone().add((-10..10).random().toDouble(),(-10..10).random().toDouble(),(-10..10).random().toDouble()),
                       conLoc2 : Location = knotLoc.clone().add((-10..10).random().toDouble(),(-10..10).random().toDouble(),(-10..10).random().toDouble())
) {
    var points: Array<Point> = arrayOf(
        Point.newPoint(ancLoc, PointTypes.ANCHOR),
        Point.newPoint(conLoc1),
        Point.newPoint(conLoc2),
        Point.newPoint(knotLoc, PointTypes.KNOT)
    )
    var continuity : Continuity = Continuity.CONNECTED
    var LUT : MutableMap<Double, Double>? = null  //a cumulative Distance LookUpTable that translates a distance in to a T value


    fun locationAtT(t: Double, world : World?) : Location{
        return pointAtT(t).toLocation(world)
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
    fun getJolt(): Point{ //P'''(t)   also no idea when you would ever need this lol, but it exists cause its possible
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

    //generates a cumulative Distance LookUpTable that translates a distance in to a T value
    fun generateDistLUT(amount : Int = (points[0].distanceTo(points[1]) + points[1].distanceTo(points[2]) + points[2].distanceTo(points[3])).toInt()) {
        if(amount < 2) return
        val s = 1.0 / amount;
        var currentT = s
        var currentDistance = 0.0
        val list: MutableMap<Double, Double> = hashMapOf( Pair(0.0, 0.0))
        for(i in 1..amount){
            if(i == amount) currentT = 1.0
            currentDistance += pointAtT(currentT-s).distanceTo(pointAtT(currentT))
            list.put(currentT, currentDistance)
            currentT += s
        }
        LUT =  list
    }

    fun lengthFromLUT() : Double {
        LUT?.let{
            val fullLength =  LUT!!.get(1.0)
            if (fullLength != null) {
                return fullLength
            }
        }
        return -1.0
    }

    //translates distance to T value based on the LookUp Table
    fun distToT(dist : Double) : Double{
        LUT?.let{
            val fullLength =  lengthFromLUT()
            if (fullLength > -1.0) {
                if((dist > 0.0) && (dist < fullLength)){
                    var underDist = 0.0
                    var upperDist = fullLength
                    var underT = 0.0
                    var upperT = 1.0
                    LUT!!.forEach(){pair->
                        if(pair.value > dist && pair.value < upperDist) {
                            upperDist = pair.value
                            upperT = pair.key
                        }
                        if(pair.value < dist && pair.value > underDist) {
                            underDist = pair.value
                            underT = pair.key
                        }
                    }
                    return ((fullLength-underDist) * ((upperT-underT)/(upperDist-underDist))) + underT
                }
            }
        }
        return -1.0
    }

    //translates percentage to T value based on the LookUp Table
    fun percToT(percentage: Double) : Double{
        val fullLength = lengthFromLUT()
        if(fullLength > -1.0){
            return distToT((fullLength/100.0)*percentage)
        }
        else return -1.0
    }


}
