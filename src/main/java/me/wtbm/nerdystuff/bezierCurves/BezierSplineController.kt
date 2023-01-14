package me.wtbm.nerdystuff.bezierCurves

import me.wtbm.nerdystuff.NerdyStuff
import me.wtbm.nerdystuff.bezierCurves.BezierTools.getMovePoint
import me.wtbm.nerdystuff.bezierCurves.BezierTools.isUsingMoveMake
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.block.data.BlockData


object BezierSplineController {
    private val plugin get() = NerdyStuff.instance
    private val splines : MutableMap<String, BezierSpline> = hashMapOf()


    fun tick1(){
        splines.forEach(){s->
            Bukkit.getOnlinePlayers().forEach() { p ->
                s.value.playAnimation(p)
            }
        }
    }

    fun tick10(){
        run moveControlPoint@{
            Bukkit.getOnlinePlayers().forEach() { p ->
                val recalculate = isUsingMoveMake(p)
                if (recalculate != null) {
                    val pair : Pair<Int,Int> = getMovePoint(p) ?: return@moveControlPoint
                    splines[recalculate]?.playerMovingPoint(p,pair.first,pair.second)
                    return@moveControlPoint
                }
            }
        }
        showBezier()
    }

    fun startAnimation(name: String) : Boolean{
        if(!splines.containsKey(name)) return false
        splines[name]?.startAnimation()
        return true
    }

    fun buildSpline(name: String, block: BlockData) : Boolean {
        if(!splines.containsKey(name)) return false
        splines[name]?.build(block)
        splines.remove(name)
        return true
    }
    fun makeSplineInt(name: String) : Boolean {
        if(!splines.containsKey(name)) return false
        splines[name]?.makeInt()
        return true
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