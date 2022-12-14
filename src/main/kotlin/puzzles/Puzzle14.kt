package puzzles

import Puzzle



class Puzzle14
{

    class Part1 : Puzzle() {
        override fun solve(lines: List<String>): String {
            val rockLines = Puzzle14.parseRockLines(lines)
            val sandSource = Puzzle14.Vector2D(500,0)

            val lowestRock = rockLines.maxOf { Math.max(it.a.y, it.b.y) }

            val rockSet = Puzzle14.computeRockSet(rockLines)

            val sandSet = mutableSetOf<Puzzle14.Vector2D>()


            while(true)
            {
                var currentSand = sandSource
                while(currentSand.y < lowestRock) {
                    var couldFall = false
                    for(fall in Puzzle14.SandMoves) {
                        val newSandPosition = currentSand + fall
                        if(!rockSet.contains(newSandPosition) && !sandSet.contains(newSandPosition) ) {
                            currentSand = newSandPosition
                            couldFall = true
                            break;
                        }
                    }
                    if(!couldFall) {
                        break;
                    }
                }
                if(currentSand.y >= lowestRock) {
                    break;
                }
                sandSet.add(currentSand)

            }
            return sandSet.size.toString()
        }

    }

    class Part2 : Puzzle() {
        override fun solve(lines: List<String>): String {
            val rockLines = Puzzle14.parseRockLines(lines)
            val sandSource = Puzzle14.Vector2D(500,0)
            val lastRockLine = getLastRockLine(rockLines, sandSource)
            rockLines.add(lastRockLine)
            val lowestRock = rockLines.maxOf { Math.max(it.a.y, it.b.y) }

            val rockSet = Puzzle14.computeRockSet(rockLines)

            val sandSet = mutableSetOf<Puzzle14.Vector2D>()


            while(true)
            {
                var currentSand = sandSource
                while(currentSand.y < lowestRock) {
                    var couldFall = false
                    for(fall in Puzzle14.SandMoves) {
                        val newSandPosition = currentSand + fall
                        if(!rockSet.contains(newSandPosition) && !sandSet.contains(newSandPosition) ) {
                            currentSand = newSandPosition
                            couldFall = true
                            break;
                        }
                    }
                    if(!couldFall) {
                        break;
                    }
                }
                sandSet.add(currentSand)
                if(currentSand == sandSource) {
                    break;
                }
            }
            return sandSet.size.toString()
        }


        private fun getLastRockLine(
            rockLines: MutableList<Puzzle14.RockLine>,
            sandSource: Puzzle14.Vector2D
        ): Puzzle14.RockLine {
            val lowestRock = rockLines.maxOf { Math.max(it.a.y, it.b.y) } + 2
            val leftMostRock = sandSource.x - (lowestRock) - 10
            val rightMostRock = sandSource.x + (lowestRock) + 10

            val lastRockLine = Puzzle14.RockLine(
                Puzzle14.Vector2D(leftMostRock - 10, lowestRock),
                Puzzle14.Vector2D(rightMostRock + 10, lowestRock)
            )
            return lastRockLine
        }

    }

    data class RockLine(val a: Vector2D, val b: Vector2D)
    data class Vector2D(val x: Int, val y: Int) {
        operator fun plus(b: Vector2D): Vector2D {
            return Vector2D(x + b.x, y + b.y)
        }

        operator fun minus(b: Vector2D): Vector2D {
            return Vector2D(x - b.x, y - b.y)
        }

        fun toLenghtOne(): Vector2D {
            check(x == 0 || y == 0) { "Vector is not fully on grid $this" }
            return Vector2D(x / Math.max(1, Math.abs(x)), y / Math.max(1, Math.abs(y)))
        }

        fun getLineTo(b: Vector2D): MutableSet<Vector2D> {
            val pointSet = mutableSetOf<Vector2D>()
            var current = this
            val direction = (b - current).toLenghtOne()
            while (b != current) {
                pointSet.add(current)
                current += direction
            }
            pointSet.add(current)
            return pointSet
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
    companion object {
        fun parseRockLines(lines: List<String>): MutableList<RockLine> {
            val rockLines = mutableListOf<RockLine>()
            for (l in lines) {
                val parts = l.split(" -> ").map { Vector2D.fromString(it) }
                var current = parts[0]
                for (p in parts.drop(1)) {
                    rockLines.add(RockLine(current, p))
                    current = p
                }
            }
            return rockLines
        }

        public fun computeRockSet(rockLines: MutableList<Puzzle14.RockLine>): MutableSet<Puzzle14.Vector2D> {
            val rockSet = mutableSetOf<Puzzle14.Vector2D>()

            for (rl in rockLines) {
                rockSet.addAll(rl.a.getLineTo(rl.b))
            }
            return rockSet
        }

        val SandMoves = listOf(Puzzle14.Vector2D.DOWN, Puzzle14.Vector2D.DOWN + Puzzle14.Vector2D.LEFT, Puzzle14.Vector2D.DOWN + Puzzle14.Vector2D.RIGHT)


    }
}

