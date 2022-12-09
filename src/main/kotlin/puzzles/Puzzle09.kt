package puzzles

import Puzzle
import kotlin.math.absoluteValue

class Puzzle09P1 : Puzzle() {
    override fun solve(lines: List<String>): String {

        val steps = parseSteps(lines)

        var positionHead = Vector2D(0, 0)
        var positionTail = positionHead
        var positionsTail = mutableListOf<Vector2D>()
        positionsTail.add(positionTail)

        steps.forEach { step ->

            for (i in 0 until step.count) {
                positionHead += step.vector
                if (!positionTail.adjacent(positionHead)) {
                    positionTail += (positionHead - positionTail).cropToMaxStep()
                    positionsTail.add(positionTail)
                }
            }
        }

        return positionsTail.toSet().size.toString()
    }
}

data class Steps(val direction: Char, val vector: Vector2D, val count: Int)

data class Vector2D(val x: Int, val y: Int) {
    fun adjacent(b: Vector2D): Boolean {
        val diff = (this - b)
        return diff.x.absoluteValue <= 1 && diff.y.absoluteValue <= 1
    }

    fun cropToMaxStep(): Vector2D {
        return Vector2D(Math.min(Math.max(-1, x), 1), Math.min(Math.max(-1, y), 1))
    }

    operator fun plus(b: Vector2D) = Vector2D(x + b.x, y + b.y)
    operator fun minus(b: Vector2D) = Vector2D(x - b.x, y - b.y)
}

class Puzzle09P2 : Puzzle() {
    override fun solve(lines: List<String>): String {
        val steps = parseSteps(lines)

        var start = Vector2D(0, 0)

        val positionMap = (0 until 10).associateWith { start }
        val positionRecord = positionMap.mapValues { mutableListOf(it.value) }

        steps.forEach { step ->
            for (i in 0 until step.count) {

                val positions = positionRecord.getValue(0)
                positions.add(positions.last() + step.vector)
                for(knot in 1 until 10)
                {
                    val positionPreviousKnot = positionRecord.getValue(knot - 1).last()
                    val positionsCurrentKnot = positionRecord.getValue(knot)
                    val currentPositionCurrentKnot = positionsCurrentKnot.last()
                    if(!currentPositionCurrentKnot.adjacent(positionPreviousKnot))
                    {
                        val currentKnotMovement = (positionPreviousKnot - currentPositionCurrentKnot).cropToMaxStep()
                        positionsCurrentKnot.add(currentPositionCurrentKnot + currentKnotMovement)
                    }
                }
            }
        }
        return positionRecord.getValue(9).toSet().size.toString()
    }

}

private fun parseSteps(lines: List<String>) = lines.map {
    val parts = it.split(" ")
    val direction = parts[0][0]
    val vector = when (direction) {
        'L' -> Vector2D(-1, 0)
        'U' -> Vector2D(0, -1)
        'R' -> Vector2D(1, 0)
        'D' -> Vector2D(0, 1)
        else -> error("wrong direction")
    }
    val count = parts[1].toInt()
    Steps(direction, vector, count)
}