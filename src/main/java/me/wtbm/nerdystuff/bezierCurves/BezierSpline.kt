package me.wtbm.nerdystuff.bezierCurves


import org.bukkit.Color.fromRGB
import org.bukkit.Location
import org.bukkit.Particle
import org.bukkit.World
import org.bukkit.entity.Player

class BezierSpline(loc: Location) {
    private val cubicBezierCurve: MutableList<CubicBezierCurve> = mutableListOf(CubicBezierCurve(loc))
    private val world : World? = loc.world
    private val generatedLocations : MutableMap<Location, Particle.DustOptions> = hashMapOf()
    private val extraLocations : MutableMap<Location, Particle.DustOptions> = hashMapOf()
    init{
        generateLocList(0.06)
        generateExtraLocList(10)
    }

    fun removeLastCurve() : Boolean {
        if(cubicBezierCurve.size <= 1) return false
        cubicBezierCurve.removeLast()
        generateLocList(0.06)
        generateExtraLocList(10)
        return true
    }

    fun lastLoc() : Location{
        return cubicBezierCurve.last().points[3].toLocation(world)
    }
    fun firstLoc() : Location{
        return cubicBezierCurve.first().points[0].toLocation(world)
    }

    fun locAtU(u : Double) : Location{
        val t = u%1
        val index = u.toInt() % cubicBezierCurve.size
        return cubicBezierCurve[index].locationAtT(t, world)
    }

    fun addCurve(c: Continuity){
        val last = cubicBezierCurve.last().points[3]
        if(c == Continuity.CONNECTED){
            cubicBezierCurve.add(CubicBezierCurve(last.toLocation(world)))
        }
        else if(c == Continuity.ALIGNED){
            cubicBezierCurve.add(CubicBezierCurve(last.toLocation(world),last.getAlignedFrom(cubicBezierCurve.last().points[2]).toLocation(world)))
        }
        else if(c == Continuity.MIRRORED){
            cubicBezierCurve.add(CubicBezierCurve(last.toLocation(world),last.getMirrorFrom(cubicBezierCurve.last().points[2]).toLocation(world)))
        }
        generateLocList(0.06)
        generateExtraLocList(10)
    }

    //a0 | b1: 255/145/249    b2:226/0/204    b3:119/39/111
    //a1 | b1: 146/241/255    b2:0/182/228    b3:39/87/119
    fun getColor(a: Int, b: Int) : Particle.DustOptions{
        if(b == 1) return Particle.DustOptions(fromRGB(255-(109*a),145+(96*a),249+(6*a)), 1.7f)
        if(b == 2) return Particle.DustOptions(fromRGB(226-(226*a),182*a,204+(24*a)), 1.0f)
        else return  Particle.DustOptions(fromRGB(119-(80*a),39+(48*a),111+(8*a)), 1.0f)
    }

    fun generateExtraLocList(amount: Int){
        extraLocations.clear()
        for(i in 0 until cubicBezierCurve.size){
            val cd : Int = i%2
            val points = cubicBezierCurve[i].points
            val step : Double = 1.0/amount
            var currentT = 0.0
            for(j in 1..amount){
                if (j == amount) currentT = 1.0
                extraLocations.put(points[0].lerpTo(points[1],currentT).toLocation(world), getColor(cd, 2))
                extraLocations.put(points[3].lerpTo(points[2],currentT).toLocation(world), getColor(cd, 2))
                currentT += step
            }
            points.forEach { point ->
                extraLocations.put(point.toLocation(world), getColor(cd, 1))
            }
        }
    }

    fun generateLocList(amount : Int, basedOnU : Boolean = true){
        generatedLocations.clear()
        if(basedOnU) {
            var s = cubicBezierCurve.size.toDouble() / amount.toDouble()
            var currentU = 0.0
            for (i in 1..amount) {
                if (i == amount) currentU = cubicBezierCurve.size.toDouble()
                val color= getColor((currentU.toInt()%2),3)
                generatedLocations.put(locAtU(currentU), color)
                currentU += s
            }
        }
        else{

        }
    }

    fun generateLocList(Interval : Double, basedOnU : Boolean = true){
        generatedLocations.clear()
        if(basedOnU){
            var currentU = 0.0
            while(currentU <= cubicBezierCurve.size){
                val color= getColor((currentU.toInt()%2),3)
                generatedLocations.put(locAtU(currentU), color)
                currentU += Interval
            }
        }
        else{

        }
    }

    fun showToPlayer(p: Player){
        generatedLocations.forEach(){loc->
            p.spawnParticle(Particle.REDSTONE,loc.key, 50,loc.value)
        }
        extraLocations.forEach(){loc->
            if(p.location.distanceSquared(loc.key) < 400) //25^2 = 625  //20^2 = 400
                p.spawnParticle(Particle.REDSTONE,loc.key, 50,loc.value)
        }
    }
}