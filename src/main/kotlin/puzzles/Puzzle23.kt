package puzzles

import Puzzle

class Puzzle23 {
    class Part1 : Puzzle() {
        override fun solve(lines: List<String>): String {
            val elves = mutableSetOf<Vector2D>()
            lines.forEachIndexed { y, l ->
                l.forEachIndexed { x, c ->
                    if (c == '#') {
                        elves.add(Vector2D(x, y))
                    }
                }
            }

            for (i in 0 until 10) {
                val movingElves = elves.filter { it.surroundings().any { elves.contains(it) } }
                val elvesAndMoves1 = movingElves.associateWith { proposeMove(it, elves, i) }
                val elvesAndMoves = elvesAndMoves1.filter { it.value != null }
                val allowedDestinations =
                    elvesAndMoves.values.groupingBy { it }.eachCount().filter { it.value == 1 }.keys
                val filterValues = elvesAndMoves.filterValues { allowedDestinations.contains(it) }
                elves.removeAll(filterValues.keys)
                elves.addAll(filterValues.values.filterNotNull())
            }
            val min = elves.reduce { acc, vector2D -> acc.minimum(vector2D) }
            val max = elves.reduce { acc, vector2D -> acc.maximum(vector2D) }
            val diagonal = max - min
            val tiles = (diagonal.x + 1) * (diagonal.y + 1) - elves.size
            return tiles.toString()
        }
    }

    class Part2 : Puzzle() {
        override fun solve(lines: List<String>): String {
            val elves = mutableSetOf<Vector2D>()
            lines.forEachIndexed { y, l ->
                l.forEachIndexed { x, c ->
                    if (c == '#') {
                        elves.add(Vector2D(x, y))
                    }
                }
            }

            for (i in 0 until 100000000) {
                val movingElves = elves.filter { it.surroundings().any { elves.contains(it) } }
                val elvesAndMoves1 = movingElves.associateWith { proposeMove(it, elves, i) }
                val elvesAndMoves = elvesAndMoves1.filter { it.value != null }
                if (elvesAndMoves.isEmpty()) {
                    return (i + 1).toString();
                }
                val allowedDestinations =
                    elvesAndMoves.values.groupingBy { it }.eachCount().filter { it.value == 1 }.keys
                val filterValues = elvesAndMoves.filterValues { allowedDestinations.contains(it) }
                elves.removeAll(filterValues.keys)
                elves.addAll(filterValues.values.filterNotNull())
            }
            return "ERROR"
        }

    }

    companion object {

        fun proposeMove(currentElve: Vector2D, elves: Set<Vector2D>, offset: Int): Vector2D? {
            val moves = listOf(
                Pair(
                    setOf(
                        currentElve + Vector2D.NORTH,
                        currentElve + Vector2D.NORTH + Vector2D.EAST,
                        currentElve + Vector2D.NORTH + Vector2D.WEST
                    ), currentElve + Vector2D.NORTH
                ),
                Pair(
                    setOf(
                        currentElve + Vector2D.SOUTH,
                        currentElve + Vector2D.SOUTH + Vector2D.EAST,
                        currentElve + Vector2D.SOUTH + Vector2D.WEST
                    ), currentElve + Vector2D.SOUTH
                ),
                Pair(
                    setOf(
                        currentElve + Vector2D.WEST,
                        currentElve + Vector2D.WEST + Vector2D.NORTH,
                        currentElve + Vector2D.WEST + Vector2D.SOUTH
                    ), currentElve + Vector2D.WEST
                ),
                Pair(
                    setOf(
                        currentElve + Vector2D.EAST,
                        currentElve + Vector2D.EAST + Vector2D.NORTH,
                        currentElve + Vector2D.EAST + Vector2D.SOUTH
                    ), currentElve + Vector2D.EAST
                ),
            )
            val effectiveOffset = offset % moves.size
            val rotatedMoves = moves.subList(effectiveOffset, moves.size) + moves.subList(0, effectiveOffset)
            return rotatedMoves.firstOrNull() { m -> m.first.all { !elves.contains(it) } }?.second
        }

        data class Vector2D(val x: Int, val y: Int) {
            operator fun plus(b: Vector2D): Vector2D {
                return Vector2D(x + b.x, y + b.y)
            }

            operator fun times(b: Int): Vector2D {
                return Vector2D(x * b, y * b)
            }

            operator fun minus(b: Vector2D): Vector2D {
                return Vector2D(x - b.x, y - b.y)
            }

            fun minimum(b: Vector2D): Vector2D {
                return Vector2D(Math.min(x, b.x), Math.min(y, b.y))
            }

            fun maximum(b: Vector2D): Vector2D {
                return Vector2D(Math.max(x, b.x), Math.max(y, b.y))
            }

            fun surroundings(): Set<Vector2D> {
                return setOf(
                    this + WEST + NORTH,
                    this + NORTH,
                    this + EAST + NORTH,
                    this + WEST,
                    this + EAST,
                    this + WEST + SOUTH,
                    this + SOUTH,
                    this + EAST + SOUTH,
                )
            }


            companion object {
                val WEST = Vector2D(-1, 0)
                val NORTH = Vector2D(0, -1)
                val EAST = Vector2D(1, 0)
                val SOUTH = Vector2D(0, 1)
            }
        }
    }
}