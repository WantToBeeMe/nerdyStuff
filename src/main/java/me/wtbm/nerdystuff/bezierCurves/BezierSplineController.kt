package me.wtbm.nerdystuff.bezierCurves

import me.wtbm.nerdystuff.NerdyStuff
import me.wtbm.nerdystuff.bezierCurves.BezierTools.isHoldingMoveMake
import org.bukkit.Bukkit
import org.bukkit.Color
import org.bukkit.Location
import org.bukkit.Particle

object BezierSplineController {
    private val plugin get() = NerdyStuff.instance
    private val splines : MutableMap<String, BezierSpline> = hashMapOf()

    fun tick01(){

        Bukkit.getOnlinePlayers().forEach(){p->
            if(isHoldingMoveMake(p)){
                val loc = p.eyeLocation.add( p.eyeLocation.direction.multiply(12) )
                val point = Location(p.world, 34.0 , 125.0 , 86.0)
                p.spawnParticle(Particle.REDSTONE,point,50, Particle.DustOptions(
                    Color.fromRGB(255 , 200 , 0), 1.7f))
                /*
                with P(-6, 1, 2) as point we here you check the distance
                L: { as the line to check the distance from
                    x = 2t + 2
                    y = 3t + 3
                    z = 7t + 5
                    }
                s = (2,3,7) the slope
                s*distance = s*P
                (2,3,7)*(x,y,z) = (2,3,7)*(-6,1,2)
                (2x,3y,7z) = (-12,3,14)
                2x + 3y + 7z = 5
                2(2t + 2) + 3(3t + 3) 7(7t + 5) = 5
                59t = 5-48
                so the corresponding point on L to P is at t=((5-48) / 59)
                 */
                Bukkit.getLogger().info("${loc.blockX} , ${loc.blockY} , ${loc.blockZ}")
            }
        }
    }

    fun tick10(){
        showBezier()
    }

    fun removeLast(name: String) : Boolean{
        if(!splines.containsKey(name)) return false
        return splines[name]?.removeLastCurve() == true
    }

    fun addCurveToSpline(name: String, con: Continuity = Continuity.CONNECTED) : Boolean{
        if(splines.containsKey(name)){
            splines.get(name)?.addCurve(con)
            return true
        }
        return false
    }

    fun getSplines() :MutableMap<String, BezierSpline> = splines

    fun tryDeleteSpline(name: String) : Boolean{
        if(splines.containsKey(name)){
            splines.remove(name)
            return true
        }
        return false
    }

    fun createNewSpline(loc: Location, name: String) : Boolean{
        if(splines.containsKey(name)) return false
        else {
            splines[name] = BezierSpline(loc)
            splines[name]?.addCurve(Continuity.MIRRORED)
            return true
        }
    }

    fun showBezier(){
        Bukkit.getOnlinePlayers().forEach(){p->
            splines.forEach(){spline->
                spline.value.showToPlayer(p)
            }
        }
    }
}