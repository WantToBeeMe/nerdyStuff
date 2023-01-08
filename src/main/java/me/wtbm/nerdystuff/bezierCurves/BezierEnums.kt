package me.wtbm.nerdystuff.bezierCurves

enum class PointTypes {
    ANCHOR, //start of a Cubic curve
    CONTROL, // all other points inside a BezierCurve
    KNOT, // end of a Cubic curve //Note that in this case all Knot point are also Control points
    VELOCITY, ACCELERATION, VECTOR, JOLT // after computing, not of any physical use
}

enum class Continuity {
    CONNECTED,//c0 & g0   a.k.a. BROKEN  |  Knot is in the same location (minimum for a spline)
    ALIGNED,  //c0 & g1                  |  Knots have the same angle
    MIRRORED  //c1 & g1                  | Knots have the same angle & control length
    //c2+ & g2+  not implemented, also not planing to

    //motion continues
    //c0 = knot is continues in position (same as g0)
    //c1 = knot is continues in velocity
    //c2 = knot is continues in acceleration
    //c3 = knot is continues in jolt (who the fuck uses jolts)

    //geometric continues
    //g0 = knot is continues in position (same as c0)
    //g1 = knot is continues in velocity direction
    //g2 = knot is continues in curvature
    //g3 = ¯\(°_o)/¯
}