package me.wtbm.nerdystuff.bezierCurves

import me.wtbm.nerdystuff.NerdyStuff
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Particle

object BezierSplineController {
    private val plugin get() = NerdyStuff.instance
    private val splines : MutableMap<String, BezierSpline> = hashMapOf()

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

    fun getSplines() :MutableMap<String, BezierSpline>{
        return splines
    }
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