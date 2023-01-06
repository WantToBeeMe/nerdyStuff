package me.wtbm.nerdystuff.bezierCurves

import org.bukkit.Location

class BezierSpline(loc: Location) {
    val cubicBezierCurve: MutableList<CubicBezierCurve> = mutableListOf(CubicBezierCurve(loc))
}