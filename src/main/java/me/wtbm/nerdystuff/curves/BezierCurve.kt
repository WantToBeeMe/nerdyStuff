package me.wtbm.nerdystuff.curves

import org.bukkit.Color
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.Particle.DustOptions
import org.bukkit.entity.Player


class BezierCurve(lastLocation : Location) {
    private val baseLines : MutableList<Line> = mutableListOf<Line>();
    private var colorCurve = Color.fromRGB(255, 0, 255)
    private var colorPivot = Color.fromRGB(255, 245, 70)
    private var curveLocations : MutableList<Location> = mutableListOf<Location>();
    private var pivotLocations : MutableList<Location> = mutableListOf<Location>(lastLocation);
    private var spread : Int = 8
    private var spreadMode: SpreadMode = SpreadMode.MOTION

    fun getPivotSize() : Int{
        return pivotLocations.size
    }
    fun placePath(block : Material) {
        curveLocations.forEach(){ loc->
            loc.world?.setBlockData(loc, block.createBlockData())
        }
    }

    fun setSpread(new : Int){
        spread = new
    }
    fun setSpreadMode(sm : SpreadMode){
        spreadMode = sm
    }

    fun addLoc(loc : Location) {
        val line : Line = Line.new(pivotLocations.last(), loc)
        pivotLocations.add(loc)
        baseLines.add(line)
        generateLocList()
    }
    fun undoLast(): Boolean{
        if(baseLines.isEmpty()) return false
        pivotLocations.removeLast()
        baseLines.removeLast()
        generateLocList()
        return true
    }

    fun toStrings() : List<String> {
        val list : MutableList<String> = mutableListOf<String>();
        baseLines.forEach(){
            list.add(it.toString())
        }
        return list
    }


    fun setColor(r: Int, g: Int, b: Int, part : Part = Part.CURVE) : Boolean{
        if(r in 0..255 && g in 0..255 && b in 0..255) {
            if(part == Part.CURVE || part == Part.ALL) colorCurve = Color.fromRGB(r, g, b)
            if(part == Part.PIVOT || part == Part.ALL) colorPivot = Color.fromRGB(r, g, b)
            return true;
        }
        else return false;
    }

    fun getLocation(T: Double): Location{
        if(baseLines.isEmpty())return pivotLocations.last()
        var lines = baseLines
        while(lines.size != 1){
            val tempLines : MutableList<Line> = mutableListOf<Line>()
            for(i in 1 until lines.size){
                tempLines.add(Line.new(lines[i-1].getLocation(T), lines[i].getLocation(T)))
            }
            lines = tempLines
        }
        return lines[0].getLocation(T)
    }

    fun generateLocList(amount : Int = baseLines.size *spread){
        if(baseLines.isEmpty()){
            curveLocations = mutableListOf<Location>(pivotLocations.last());
        }

        else{
            val tempList : MutableList<Location> = mutableListOf<Location>();
            val interval : Double = 1.0 / amount
            var current = 0.0
            while(current <= 1){
                tempList.add(getLocation(current))
                current += interval
            }
            curveLocations = tempList
        }
    }

    fun showCurve(p: Player, part : Part = Part.ALL) {
        if(part == Part.CURVE || part == Part.ALL) curveLocations.forEach(){ loc->
            p.spawnParticle(Particle.REDSTONE, loc, 50, DustOptions(colorCurve, 1.0f));
        }
        if(part == Part.PIVOT || part == Part.ALL) pivotLocations.forEach(){ loc->
            p.spawnParticle(Particle.REDSTONE, loc, 50, DustOptions(colorPivot, 1.0f));
        }

    }
}

enum class SpreadMode {
    MOTION, EVEN
}

enum class Part {
    CURVE, PIVOT, ALL
}