package puzzles

import Puzzle

class Puzzle15 {
    class Part1 : Puzzle() {
        override fun solve(lines: List<String>): String {
            val yTest = lines.first().toInt()
            val sbList = parseSensorBeaconList(lines)

            val setClearedOnTestLine = mutableSetOf<Vector2D>()

            for (sb in sbList) {
                val itemsleft = sb.clearanceRadius() - Math.abs(sb.sensor.y - yTest)
                if (itemsleft >= 0) {
                    val start = -itemsleft
                    val end = itemsleft
                    for (i in start..end)
                        setClearedOnTestLine.add(Vector2D(sb.sensor.x + i, yTest))
                }
            }

            val removedKnown = setClearedOnTestLine - sbList.flatMap { setOf(it.sensor, it.beacon) }.toSet()

            return removedKnown.size.toString()
        }
    }

    data class SensorBeaconPair(val sensor: Vector2D, val beacon: Vector2D) {
        fun clearanceRadius(): Int {
            return sensor.manhattanDistance(beacon)
        }
    }

    data class Vector2D(val x: Int, val y: Int) {
        operator fun plus(b: Vector2D): Vector2D {
            return Vector2D(x + b.x, y + b.y)
        }

        operator fun minus(b: Vector2D): Vector2D {
            return Vector2D(x - b.x, y - b.y)
        }

        fun manhattanDistance(b: Vector2D): Int {
            val dist = this - b
            return Math.abs(dist.x) + Math.abs(dist.y)
        }

        fun toLengthOne(): Vector2D {
            //check(x == 0 || y == 0) { "Vector is not fully on grid $this" }
            return Vector2D(x / Math.max(1, Math.abs(x)), y / Math.max(1, Math.abs(y)))
        }

        companion object {
            val LEFT = Vector2D(-1, 0)
            val UP = Vector2D(0, -1)
            val RIGHT = Vector2D(1, 0)
            val DOWN = Vector2D(0, 1)

            fun fromString(str: String): Vector2D {
                val parts = str.split(",")
                return Vector2D(parts[0].toInt(), parts[1].toInt())
            }
        }
    }

    class Part2 : Puzzle() {
        data class Range(val start: Int, val end: Int) {
            fun substract(other: Range): List<Range> {
                return listOfNotNull(
                    if (start < other.start) {
                        Range(start, Math.min(other.start - 1, end))
                    } else {
                        null
                    },
                    if (end > other.end) {
                        Range(Math.max(other.end + 1, start), end)
                    } else {
                        null
                    }
                )
            }
        }

        data class RangeList(var set: List<Range>) {
            fun substract(r: Range) {
                set = set.flatMap { it.substract(r) }
            }
        }

        override fun solve(lines: List<String>): String {
            val size = lines.first().toInt()
            val factor = 4000000L
            val sbList = parseSensorBeaconList(lines)

            val blocked = sbList.flatMap { setOf(it.sensor, it.beacon) }.toSet()

            for (yTest in 0 until size) {
                val rangeSet = RangeList(listOf(Range(0, size)))

                blocked.filter { it.y == yTest }.forEach {
                    rangeSet.substract(Range(it.x, it.x))
                }
                for (sb in sbList) {
                    val itemsleft = sb.clearanceRadius() - Math.abs(sb.sensor.y - yTest)
                    if (itemsleft >= 0) {
                        val start = Math.min(sb.sensor.x - itemsleft, size)
                        val end = Math.max(sb.sensor.x + itemsleft, 0)
                        rangeSet.substract(Range(sb.sensor.x - itemsleft, sb.sensor.x + itemsleft))
                    }
                }
                if (rangeSet.set.isNotEmpty()) {
                    check(rangeSet.set.size == 1)
                    check(rangeSet.set.first().start == rangeSet.set.first().end)
                    return (rangeSet.set.first().start * factor + yTest).toString()
                }
            }
            return ""
        }
    }
}

private fun parseSensorBeaconList(lines: List<String>): MutableList<Puzzle15.SensorBeaconPair> {
    val sbList = mutableListOf<Puzzle15.SensorBeaconPair>()

    for (l in lines.drop(1)) {
        val parts = l.split("at x=")
        val partsS = parts[1].split(", y=")
        val partsB = parts[2].split(", y=")
        val sensorY = partsS[1].split(":")[0]

        val sensor = Puzzle15.Vector2D(partsS[0].toInt(), sensorY.toInt())
        val beacon = Puzzle15.Vector2D(partsB[0].toInt(), partsB[1].toInt())
        sbList.add(Puzzle15.SensorBeaconPair(sensor, beacon))
    }
    return sbList
}