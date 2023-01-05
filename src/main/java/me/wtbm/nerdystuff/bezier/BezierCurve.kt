package me.wtbm.nerdystuff.bezier

import org.bukkit.*
import org.bukkit.Particle.DustOptions
import org.bukkit.entity.Player


class BezierCurve(lastLocation : Location) {
    private var colorCurve = Color.fromRGB(255, 0, 255)
    private var colorPivot = Color.fromRGB(255, 245, 70)
    private var generatedLocations : MutableList<Location> = mutableListOf<Location>();
    private var pivotLocations : MutableList<Location> = mutableListOf<Location>(lastLocation);
    private var interval : Int = 8
    private var intervalMode: IntervalMode = IntervalMode.MOTION

    fun getPivotAmount() : Int{
        return pivotLocations.size
    }
    fun placePath(block : Material) {
        generatedLocations.forEach(){ loc->
            loc.world?.setBlockData(loc, block.createBlockData())
        }
    }

    fun setInterval(new : Int){
        interval = new
        generateLocList()

    }
    fun setIntervalMode(sm : IntervalMode){
        intervalMode = sm
        generateLocList()
    }

    fun addLoc(loc : Location) {
        pivotLocations.add(loc)
        generateLocList()
    }
    fun undoLast(): Boolean{
        if(pivotLocations.size <= 1) return false
        pivotLocations.removeLast()
        generateLocList()
        return true
    }

    fun toStrings() : List<String> {
        val list : MutableList<String> = mutableListOf<String>();
        for(i in 1 until pivotLocations.size){
            val start = pivotLocations[i-1]
            val end = pivotLocations[i]
            val startX:Double = Math.round(start.x * 10.0) / 10.0
            val startY:Double = Math.round(start.y * 10.0) / 10.0
            val startZ:Double = Math.round(start.z * 10.0) / 10.0
            val endX:Double = Math.round(end.x * 10.0) / 10.0
            val endY:Double = Math.round(end.y * 10.0) / 10.0
            val endZ:Double = Math.round(end.z * 10.0) / 10.0
            list.add("$startX $startY $startZ ${ChatColor.YELLOW}->${ChatColor.RESET} $endX $endY $endZ ")
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
        var pivots = pivotLocations
        while(pivots.size > 1){
            val tempPivots : MutableList<Location> = mutableListOf<Location>()
            for(i in 1 until pivots.size){
                tempPivots.add(locBetween2Locations(pivots[i-1], pivots[i], T))
            }
            pivots = tempPivots
        }
        return pivots[0]
    }
    fun locBetween2Locations(startLoc: Location, endLoc: Location, T: Double): Location{
        val linearX = (endLoc.x - startLoc.x)*T + startLoc.x
        val linearY = (endLoc.y - startLoc.y)*T + startLoc.y
        val linearZ = (endLoc.z - startLoc.z)*T + startLoc.z
        return Location(startLoc.world,linearX, linearY, linearZ)
    }
    fun generateLocListWithExtra(extraLoc : Location){
        pivotLocations.add(extraLoc)
        generateLocList()
        pivotLocations.remove(extraLoc)
    }
    fun generateLocList(amount : Int = pivotLocations.size *interval){
        if(pivotLocations.isEmpty()) generatedLocations = pivotLocations
        //implement with interval modes
        else{
            val tempList : MutableList<Location> = mutableListOf<Location>();
            val interval : Double = 1.0 / amount
            var current = 0.0
            while(current <= 1){
                tempList.add(getLocation(current))
                current += interval
            }
            generatedLocations = tempList
        }
    }

    fun showCurve(p: Player, part : Part = Part.ALL) {
        if(part == Part.CURVE || part == Part.ALL) generatedLocations.forEach(){ loc->
            p.spawnParticle(Particle.REDSTONE, loc, 50, DustOptions(colorCurve, 1.0f));
        }
        if(part == Part.PIVOT || part == Part.ALL) pivotLocations.forEach(){ loc->
            p.spawnParticle(Particle.REDSTONE, loc, 50, DustOptions(colorPivot, 1.0f));
        }

    }
}

