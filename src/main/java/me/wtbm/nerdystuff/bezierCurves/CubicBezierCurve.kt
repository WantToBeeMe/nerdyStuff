package me.wtbm.nerdystuff.bezierCurves

import me.wtbm.nerdystuff.abcFormula
import org.bukkit.Bukkit
import org.bukkit.Location
import kotlin.math.max
import kotlin.math.min

class CubicBezierCurve(ancLoc : Location,
                       conLoc1 : Location = ancLoc.clone().add((-10..10).random().toDouble(),(-10..10).random().toDouble(),(-10..10).random().toDouble()),
                       knotLoc : Location = ancLoc.clone().add((-10..10).random().toDouble(),(-10..10).random().toDouble(),(-10..10).random().toDouble()),
                       conLoc2 : Location = knotLoc.clone().add((-10..10).random().toDouble(),(-10..10).random().toDouble(),(-10..10).random().toDouble())
) {
    var points: Array<Point> = arrayOf(
        Point.newPoint(ancLoc, PointTypes.ANCHOR, 20),
        Point.newPoint(conLoc1),
        Point.newPoint(conLoc2),
        Point.newPoint(knotLoc, PointTypes.KNOT,120)
    )
    var continuity : Continuity = Continuity.CONNECTED
    var LUT : MutableMap<Double, Double>? = null  //a cumulative Distance LookUpTable that translates a distance in to a T value


    fun makeInt(){
        points[0].makeInt()
        points[1].makeInt()
        points[2].makeInt()
        points[3].makeInt()
    }

    fun pointAtT(t: Double) : Point = //P(t)
                points[0] * (-t*t*t +3*t*t -3*t + 1) +
                points[1] * (3*t*t*t -6*t*t +3*t) +
                points[2] * (-3*t*t*t +3*t*t) +
                points[3] * (t*t*t)

    fun velocityAtT(t: Double):Point = //P'(t)
                points[0] * (-3*t*t +6*t -3) +
                points[1] * (9*t*t -12*t +3) +
                points[2] * (-9*t*t +6*t) +
                points[3] * (3*t*t)

    fun accelerationAtT(t: Double):Point = //P''(t)
                points[0] * (-6*t +6) +
                points[1] * (18*t -12) +
                points[2] * (-18*t +6) +
                points[3] * (6*t)


    fun getJolt(): Point = //P'''(t)   also no idea when you would ever need this lol, but it exists cause its possible
                points[0] * (-6.0) +
                points[1] * (18.0) +
                points[2] * (-18.0) +
                points[3] * (6.0)

    fun getExtremeTs():List<Double>{
        val ax = points[0].x*-3.0 + points[1].x*9.0 + points[2].x*-9.0 + points[3].x*3.0
        val bx = points[0].x*6.0 + points[1].x*-12.0 + points[2].x*6.0
        val cx = points[0].x*-3.0 + points[1].x*3.0

        val ay = points[0].y*-3.0 + points[1].y*9.0 + points[2].y*-9.0 + points[3].y*3.0
        val by = points[0].y*6.0 + points[1].y*-12.0 + points[2].y*6.0
        val cy = points[0].y*-3.0 + points[1].y*3.0

        val az = points[0].z*-3.0 + points[1].z*9.0 + points[2].z*-9.0 + points[3].z*3.0
        val bz = points[0].z*6.0 + points[1].z*-12.0 + points[2].z*6.0
        val cz = points[0].z*-3.0 + points[1].z*3.0

        val tx = abcFormula(ax,bx,cx)
        val ty = abcFormula(ay,by,cy)
        val tz = abcFormula(az,bz,cz)

        val returnList : MutableList<Double> = mutableListOf<Double>();
        if(tx != null){
            if( !returnList.contains(tx.first) && tx.first < 1.0 && tx.first > 0.0) returnList.add(tx.first)
            if( !returnList.contains(tx.second) && tx.second < 1.0 && tx.second > 0.0) returnList.add(tx.second)
        }
        if(ty != null){
            if( !returnList.contains(ty.first) && ty.first < 1.0 && ty.first > 0.0) returnList.add(ty.first)
            if( !returnList.contains(ty.second) && ty.second < 1.0 && ty.second > 0.0) returnList.add(ty.second)
        }
        if(tz != null){
            if( !returnList.contains(tz.first) && tz.first < 1.0 && tz.first > 0.0) returnList.add(tz.first)
            if( !returnList.contains(tz.second) && tz.second < 1.0 && tz.second > 0.0) returnList.add(tz.second)
        }

        return returnList;
    }
    fun getBoundingBox():Pair<Point,Point>{
        val start = pointAtT(0.0)
        val end = pointAtT(1.0)
        var minX = min(start.x, end.x)
        var minY = min(start.y, end.y)
        var minZ = min(start.z, end.z)
        var maxX = max(start.x, end.x)
        var maxY = max(start.y, end.y)
        var maxZ = max(start.z, end.z)

        val extremes: List<Double> = getExtremeTs()
        extremes.forEach(){ t->
            minX = min(minX, pointAtT(t).x)
            minY = min(minY, pointAtT(t).y)
            minZ = min(minZ, pointAtT(t).z)
            maxX = max(maxX, pointAtT(t).x)
            maxY = max(maxY, pointAtT(t).y)
            maxZ = max(maxZ, pointAtT(t).z)
        }
        return Pair(Point(minX,minY,minZ), Point(maxX,maxY,maxZ))
    }


    //generates a cumulative Distance LookUpTable that translates a distance in to a T value
    fun generateDistLUT(amount : Int = (points[0].distanceTo(points[1]) + points[1].distanceTo(points[2]) + points[2].distanceTo(points[3])).toInt() * 4) {
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
