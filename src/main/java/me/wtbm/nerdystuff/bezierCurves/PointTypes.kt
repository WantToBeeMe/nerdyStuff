package me.wtbm.nerdystuff.bezierCurves

enum class PointTypes {
    ANCHOR, //start of a Cubic curve
    CONTROL, // all other points inside a BezierCurve
    SPLIT, // end of a Cubic curve
    VELOCITY, ACCELERATION, LOCATION, JOLT // after computing, not of any physical use
}