package me.wtbm.nerdystuff.bezierCurves


import me.wtbm.nerdystuff.lookingAtPoint
import org.bukkit.Color.fromRGB
import org.bukkit.Location
import org.bukkit.Particle
import org.bukkit.World
import org.bukkit.block.data.BlockData
import org.bukkit.entity.Player
import org.bukkit.util.Vector

class BezierSpline(loc: Location) {
    private val cubicBezierCurves: MutableList<CubicBezierCurve> = mutableListOf(CubicBezierCurve(loc))
    private val world : World? = loc.world
    private val generatedLocations : MutableMap<Location, Particle.DustOptions> = hashMapOf()
    private val guideLineLocations : MutableMap<Location, Particle.DustOptions> = hashMapOf()
    private var animateU = -1.0 //-1.0 == false    else any positive values is the U value of the animation
    private val guideLineAmount = 6
    private val curveInterval = 0.06
    init{
        generateLocations()
    }

    fun generateLocations(){
        generateLocList(curveInterval)
        generateExtraLocList(guideLineAmount)
    }

    fun startAnimation(){
        animateU = 0.0
    }

    fun playAnimation(p: Player){
        if(animateU < 0.0) return
        val maxU = cubicBezierCurves.size
        val yellow = Particle.DustOptions(fromRGB(255,210,70), 2f)
        val red = Particle.DustOptions(fromRGB(255,10,10), 1f)
        val aqua = Particle.DustOptions(fromRGB(100,140,255), 1f)
        p.spawnParticle(Particle.REDSTONE, locationAtU(animateU), 20, yellow)
        p.spawnParticle(Particle.REDSTONE, locationAtU(animateU).add(velocityAtU(animateU).multiply(0.5)), 10, red)
        for(i in 1..12){
            p.spawnParticle(Particle.REDSTONE, locationAtU(animateU).add(velocityAtU(animateU).normalize().multiply(i.toDouble()/10)), 10, red)
        }

        val vec = velocityAtU(animateU).normalize()
        val rotation = rotationAtU(animateU)
        val rot = Vector(0.0,1.0,0.0).rotateAroundX(vec.x * rotation).rotateAroundZ(vec.z * rotation).rotateAroundY(vec.z * 90)

        for(i in 1..12){
            p.spawnParticle(Particle.REDSTONE,locationAtU(animateU).add(rot.normalize().multiply(i.toDouble()/10 )), 10, aqua)
        }

        animateU += 0.05 //add 0.1 t to the u to move the animation forward
        if(animateU > maxU) animateU = -1.0
    }

    fun removeLastCurve() : Boolean {
        if(cubicBezierCurves.size <= 1) return false
        cubicBezierCurves.removeLast()
        generateLocations()
        return true
    }

    fun lastLoc() : Location = cubicBezierCurves.last().points[3].toLocation(world)

    fun firstLoc() : Location = cubicBezierCurves.first().points[0].toLocation(world)

    fun rotationAtU(u: Double) : Int  = cubicBezierCurves[u.toInt()%cubicBezierCurves.size].pointAtT(u%1).rotation
    fun locationAtU(u : Double) : Location = cubicBezierCurves[u.toInt()%cubicBezierCurves.size].pointAtT(u%1).toLocation(world)
    fun velocityAtU(u : Double) : Vector = cubicBezierCurves[u.toInt()%cubicBezierCurves.size].velocityAtT(u%1).toVector()
    fun accelerationAtU(u : Double) : Vector = cubicBezierCurves[u.toInt()%cubicBezierCurves.size].accelerationAtT(u%1).toVector()

    //fun getJolt(u : Double) : Vector = cubicBezierCurves[u.toInt()%cubicBezierCurves.size].getJolt().toVector() //no lol, not today

    fun makeInt() = cubicBezierCurves.forEach(){ it.makeInt() ; generateLocations() }

    fun playerMovingPoint(p:Player, curve: Int, point: Int) : Boolean{ //returns false if something went wrong, using return value not required of-coarse
        val size = cubicBezierCurves.size
        if(curve >= size) return false
        if(point < 0 || point > 3) return false
        val newloc = p.eyeLocation.add(p.eyeLocation.direction.multiply(3))
        cubicBezierCurves[curve].points[point] = Point.newPoint(newloc,cubicBezierCurves[curve].points[point].type,cubicBezierCurves[curve].points[point].rotation)
        if(point == 0 && curve > 0) cubicBezierCurves[curve-1].points[3] = Point.newPoint(newloc,cubicBezierCurves[curve-1].points[3].type,cubicBezierCurves[curve-1].points[3].rotation)
        if(point == 3 && curve+1 < size) cubicBezierCurves[curve+1].points[0] = Point.newPoint(newloc,cubicBezierCurves[curve+1].points[0].type,cubicBezierCurves[curve+1].points[0].rotation)
        generateLocations()
        return true
    }

    fun addCurve(c: Continuity){
        val last = cubicBezierCurves.last().points[3]
        if(c == Continuity.CONNECTED){
            cubicBezierCurves.add(CubicBezierCurve(last.toLocation(world)))
        }
        else if(c == Continuity.ALIGNED){
            cubicBezierCurves.add(CubicBezierCurve(last.toLocation(world),last.getAlignedFrom(cubicBezierCurves.last().points[2]).toLocation(world)))
        }
        else if(c == Continuity.MIRRORED){
            cubicBezierCurves.add(CubicBezierCurve(last.toLocation(world),last.getMirrorFrom(cubicBezierCurves.last().points[2]).toLocation(world)))
        }
        generateLocations()
    }

    //a0 | b1: 255/145/249    b2:226/0/204    b3:119/39/111
    //a1 | b1: 146/241/255    b2:0/182/228    b3:39/87/119
    fun getColor(a: Int, b: Int) : Particle.DustOptions{
        return if(b == 2) Particle.DustOptions(fromRGB(226-(226*a),182*a,204+(24*a)), 1.0f)
        else Particle.DustOptions(fromRGB(119-(80*a),39+(48*a),111+(8*a)), 1.0f)
    }

    fun generateExtraLocList(amount: Int){
        guideLineLocations.clear()
        for(i in 0 until cubicBezierCurves.size){
            val cd : Int = i%2
            val points = cubicBezierCurves[i].points
            val step : Double = 1.0/(amount+2.0)
            var currentT = step
            for(j in 1..amount+1){
                guideLineLocations.put(points[0].lerpTo(points[1],currentT).toLocation(world), getColor(cd, 2))
                guideLineLocations.put(points[3].lerpTo(points[2],currentT).toLocation(world), getColor(cd, 2))
                currentT += step
            }
        }
    }

    fun generateLocList(amount : Int, basedOnU : Boolean = true){
        generatedLocations.clear()
        if(basedOnU) {
            var s = cubicBezierCurves.size.toDouble() / amount.toDouble()
            var currentU = 0.0
            for (i in 1..amount) {
                if (i == amount) currentU = cubicBezierCurves.size.toDouble()
                val color= getColor((currentU.toInt()%2),3)
                generatedLocations.put(locationAtU(currentU), color)
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
            while(currentU <= cubicBezierCurves.size){
                val color= getColor((currentU.toInt()%2),3)
                generatedLocations.put(locationAtU(currentU), color)
                currentU += Interval
            }
        }
        else{

        }
    }

    fun lookingAtWhatLoc(p: Player) : Pair<Int,Int>?{
        var cDex:Int = 0
        var pDex:Int = 0
        cubicBezierCurves.forEach(){ curve->
            curve.points.forEach { point->
                if(p.lookingAtPoint(point.toLocation(this.world)))
                    return Pair(cDex,pDex)
                pDex++
            }
            pDex = 0
            cDex++
        }
        return null
    }

    fun build(block: BlockData){
        generatedLocations.forEach(){loc->
            world?.setBlockData(loc.key, block)
        }
    }


    fun showToPlayer(p: Player){

        if(animateU < 0.0) { //if an animation is running, don't show the curve itself + the guidelines, but the control points can still be shown, they don't interfere with the animation
            generatedLocations.forEach() { loc ->
                p.spawnParticle(Particle.REDSTONE, loc.key, 20, loc.value)
            }
            guideLineLocations.forEach() { loc ->
                if (p.location.distanceSquared(loc.key) < 400) //25^2 = 625  //20^2 = 400
                    p.spawnParticle(Particle.REDSTONE, loc.key, 20, loc.value)
            }
        }

        for(i in 0 until cubicBezierCurves.size){
            val a = i%2
            var particle : Particle.DustOptions = Particle.DustOptions(fromRGB(255-(109*a),145+(96*a),249+(6*a)), 1.7f)
            cubicBezierCurves[i].points.forEach{ point->
                val loc = point.toLocation(this.world)

                if(p.location.distanceSquared(loc) < 400) {//25^2 = 625  //20^2 = 400
                    if (BezierTools.isHoldingMoveMake(p) && p.lookingAtPoint(loc)) {
                        particle = Particle.DustOptions(fromRGB(255,210,70), 2f)
                    } else {
                        particle = Particle.DustOptions(fromRGB(255-(109*a),145+(96*a),249+(6*a)), 1.7f)
                    }
                    p.spawnParticle(Particle.REDSTONE, loc, 20, particle)
                }
            }
        }

    }
}