package puzzles

import Puzzle

class Puzzle22 {
    class Part1 : Puzzle() {
        override fun solve(lines: List<String>): String {

            var parsingMap = true
            val map = mutableMapOf<Vector2D, Tile>()
            var steps = mutableListOf<Step>()

            lines.forEachIndexed { y, l ->
                if (l.isBlank()) {
                    parsingMap = false
                } else if (parsingMap) {
                    l.forEachIndexed { x, c ->
                        map[Vector2D(x, y)] = Tile.fromChar(c)
                    }
                    for (c in l) {
                        map
                    }
                } else {
                    var number = ""
                    for (c in l) {
                        if (c.isDigit()) {
                            number += c
                        } else {
                            steps.add(Step(number.toInt(), c))
                            number = ""
                        }
                    }
                    if (number.isNotBlank()) {
                        steps.add(Step(number.toInt(), 'N'))
                    }
                }
            }

            var minimumY = map.filterValues { it == Tile.PATH }.keys.minOf { it.y }

            var currentPosition = map.filterValues { it == Tile.PATH }.filterKeys { it.y == minimumY }.keys.minBy { it.x }
            var currentDirection = Vector2D.RIGHT


            for (s in steps) {
                for (i in 0 until s.count) {
                    var nextPos = currentPosition + currentDirection
                    var nextTile = map.getOrDefault(nextPos, Tile.VOID)

                    if (nextTile == Tile.VOID) {
                        val searchDirection = currentDirection * -1
                        var currentSearchPos = nextPos
                        while (true) {
                            val nextSearchPos = currentSearchPos + searchDirection
                            val nextSearchTile = map.getOrDefault(nextSearchPos, Tile.VOID)
                            if (nextSearchTile == Tile.VOID) {
                                break;
                            }
                            currentSearchPos = nextSearchPos
                        }
                        nextPos = currentSearchPos
                        nextTile = map.getOrDefault(nextPos, Tile.VOID)
                    }


                    if (nextTile == Tile.WALL) {
                        break;
                    }
                    else if (nextTile == Tile.VOID) {
                        println("error")
                    }

                    currentPosition = nextPos
                }
                currentDirection = currentDirection.turn(s.direction)
            }

            val dirVal = when(currentDirection)
            {
                Vector2D.RIGHT -> 0
                Vector2D.DOWN -> 1
                Vector2D.LEFT -> 2
                Vector2D.UP -> 3
                else -> throw RuntimeException("")
            }

            return ((currentPosition.y+1) * 1000 + (currentPosition.x+1) * 4 + dirVal).toString()
        }
    }

    class Part2 : Puzzle() {
        override fun solve(lines: List<String>): String {

            var parsingMap = true
            val map = mutableMapOf<Vector2D, Tile>()
            var steps = mutableListOf<Step>()

            lines.forEachIndexed { y, l ->
                if (l.isBlank()) {
                    parsingMap = false
                } else if (parsingMap) {
                    l.forEachIndexed { x, c ->
                        map[Vector2D(x, y)] = Tile.fromChar(c)
                    }
                    for (c in l) {
                        map
                    }
                } else {
                    var number = ""
                    for (c in l) {
                        if (c.isDigit()) {
                            number += c
                        } else {
                            steps.add(Step(number.toInt(), c))
                            number = ""
                        }
                    }
                    if (number.isNotBlank()) {
                        steps.add(Step(number.toInt(), 'N'))
                    }
                }
            }

            // my input
            val areas = mapOf<Int, Area>(
                1 to Area(1, Vector2D(2 * SIDE_LENGTH, 0 * SIDE_LENGTH)),
                2 to Area(2, Vector2D(1 * SIDE_LENGTH, 0 * SIDE_LENGTH)),
                3 to Area(3, Vector2D(1 * SIDE_LENGTH, 1 * SIDE_LENGTH)),
                4 to Area(4, Vector2D(1 * SIDE_LENGTH, 2 * SIDE_LENGTH)),
                5 to Area(5, Vector2D(0 * SIDE_LENGTH, 2 * SIDE_LENGTH)),
                6 to Area(6, Vector2D(0 * SIDE_LENGTH, 3 * SIDE_LENGTH))
            )

            val adjacentSidesList = listOf(
                AdjacentSides(areas.getValue(1), Vector2D.LEFT, areas.getValue(2), Vector2D.RIGHT),
                AdjacentSides(areas.getValue(1), Vector2D.UP, areas.getValue(6), Vector2D.DOWN),
                AdjacentSides(areas.getValue(1), Vector2D.RIGHT, areas.getValue(4), Vector2D.RIGHT),
                AdjacentSides(areas.getValue(1), Vector2D.DOWN, areas.getValue(3), Vector2D.RIGHT),
                AdjacentSides(areas.getValue(2), Vector2D.LEFT, areas.getValue(5), Vector2D.LEFT),
                AdjacentSides(areas.getValue(2), Vector2D.UP, areas.getValue(6), Vector2D.LEFT),
                AdjacentSides(areas.getValue(2), Vector2D.RIGHT, areas.getValue(1), Vector2D.LEFT),
                AdjacentSides(areas.getValue(2), Vector2D.DOWN, areas.getValue(3), Vector2D.UP),
                AdjacentSides(areas.getValue(3), Vector2D.LEFT, areas.getValue(5), Vector2D.UP),
                AdjacentSides(areas.getValue(3), Vector2D.UP, areas.getValue(2), Vector2D.DOWN),
                AdjacentSides(areas.getValue(3), Vector2D.RIGHT, areas.getValue(1), Vector2D.DOWN),
                AdjacentSides(areas.getValue(3), Vector2D.DOWN, areas.getValue(4), Vector2D.UP),
                AdjacentSides(areas.getValue(4), Vector2D.LEFT, areas.getValue(5), Vector2D.RIGHT),
                AdjacentSides(areas.getValue(4), Vector2D.UP, areas.getValue(3), Vector2D.DOWN),
                AdjacentSides(areas.getValue(4), Vector2D.RIGHT, areas.getValue(1), Vector2D.RIGHT),
                AdjacentSides(areas.getValue(4), Vector2D.DOWN, areas.getValue(6), Vector2D.RIGHT),
                AdjacentSides(areas.getValue(5), Vector2D.LEFT, areas.getValue(2), Vector2D.LEFT),
                AdjacentSides(areas.getValue(5), Vector2D.UP, areas.getValue(3), Vector2D.LEFT),
                AdjacentSides(areas.getValue(5), Vector2D.RIGHT, areas.getValue(4), Vector2D.LEFT),
                AdjacentSides(areas.getValue(5), Vector2D.DOWN, areas.getValue(6), Vector2D.UP),
                AdjacentSides(areas.getValue(6), Vector2D.LEFT, areas.getValue(2), Vector2D.UP),
                AdjacentSides(areas.getValue(6), Vector2D.UP, areas.getValue(5), Vector2D.DOWN),
                AdjacentSides(areas.getValue(6), Vector2D.RIGHT, areas.getValue(4), Vector2D.DOWN),
                AdjacentSides(areas.getValue(6), Vector2D.DOWN, areas.getValue(1), Vector2D.UP),
            )
            var currentArea = areas.getValue(2)

            // example input
//            val areas = mapOf<Int, Area>(
//                1 to Area(1, Vector2D(2 * SIDE_LENGTH, 0 * SIDE_LENGTH)),
//                2 to Area(2, Vector2D(0 * SIDE_LENGTH, 1 * SIDE_LENGTH)),
//                3 to Area(3, Vector2D(1 * SIDE_LENGTH, 1 * SIDE_LENGTH)),
//                4 to Area(4, Vector2D(2 * SIDE_LENGTH, 1 * SIDE_LENGTH)),
//                5 to Area(5, Vector2D(2 * SIDE_LENGTH, 2 * SIDE_LENGTH)),
//                6 to Area(6, Vector2D(3 * SIDE_LENGTH, 2 * SIDE_LENGTH))
//            )
//
//            val adjacentSidesList = listOf(
//                AdjacentSides(areas.getValue(1), Vector2D.LEFT, areas.getValue(3), Vector2D.UP),
//                AdjacentSides(areas.getValue(1), Vector2D.UP, areas.getValue(2), Vector2D.UP),
//                AdjacentSides(areas.getValue(1), Vector2D.RIGHT, areas.getValue(6), Vector2D.RIGHT),
//                AdjacentSides(areas.getValue(1), Vector2D.DOWN, areas.getValue(4), Vector2D.UP),
//                AdjacentSides(areas.getValue(2), Vector2D.LEFT, areas.getValue(6), Vector2D.DOWN),
//                AdjacentSides(areas.getValue(2), Vector2D.UP, areas.getValue(1), Vector2D.UP),
//                AdjacentSides(areas.getValue(2), Vector2D.RIGHT, areas.getValue(3), Vector2D.LEFT),
//                AdjacentSides(areas.getValue(2), Vector2D.DOWN, areas.getValue(5), Vector2D.DOWN),
//                AdjacentSides(areas.getValue(3), Vector2D.LEFT, areas.getValue(2), Vector2D.RIGHT),
//                AdjacentSides(areas.getValue(3), Vector2D.UP, areas.getValue(1), Vector2D.LEFT),
//                AdjacentSides(areas.getValue(3), Vector2D.RIGHT, areas.getValue(4), Vector2D.LEFT),
//                AdjacentSides(areas.getValue(3), Vector2D.DOWN, areas.getValue(5), Vector2D.LEFT),
//                AdjacentSides(areas.getValue(4), Vector2D.LEFT, areas.getValue(3), Vector2D.RIGHT),
//                AdjacentSides(areas.getValue(4), Vector2D.UP, areas.getValue(1), Vector2D.DOWN),
//                AdjacentSides(areas.getValue(4), Vector2D.RIGHT, areas.getValue(6), Vector2D.UP),
//                AdjacentSides(areas.getValue(4), Vector2D.DOWN, areas.getValue(5), Vector2D.UP),
//                AdjacentSides(areas.getValue(5), Vector2D.LEFT, areas.getValue(3), Vector2D.DOWN),
//                AdjacentSides(areas.getValue(5), Vector2D.UP, areas.getValue(4), Vector2D.DOWN),
//                AdjacentSides(areas.getValue(5), Vector2D.RIGHT, areas.getValue(6), Vector2D.LEFT),
//                AdjacentSides(areas.getValue(5), Vector2D.DOWN, areas.getValue(2), Vector2D.DOWN),
//                AdjacentSides(areas.getValue(6), Vector2D.LEFT, areas.getValue(5), Vector2D.RIGHT),
//                AdjacentSides(areas.getValue(6), Vector2D.UP, areas.getValue(4), Vector2D.RIGHT),
//                AdjacentSides(areas.getValue(6), Vector2D.RIGHT, areas.getValue(1), Vector2D.RIGHT),
//                AdjacentSides(areas.getValue(6), Vector2D.DOWN, areas.getValue(2), Vector2D.LEFT),
//            )
//            var currentArea = areas.getValue(1)

            val adjacentSides = adjacentSidesList.groupBy { it.areaA }.mapValues { it.value.associateBy { it.sideA } }

            for(adsl in adjacentSidesList) {
                val c = adjacentSides.getValue(adsl.areaB).getValue(adsl.sideB)
                check(c.areaB == adsl.areaA)
                check(c.sideB == adsl.sideA)
            }



            areas.forEach { it.value.neighbors = adjacentSides.getValue(it.value) }

            var currentPosition = Vector2D(0, 0)
            var currentDirection = Vector2D.RIGHT


            for (s in steps) {
                for (i in 0 until s.count) {
                    var nextPos = currentPosition + currentDirection
                    var nextArea = currentArea
                    var nextDirection = currentDirection

                    if (nextPos.x < 0 || nextPos.y < 0 || nextPos.x >= SIDE_LENGTH || nextPos.y >= SIDE_LENGTH) {
                        val newArea = currentArea.neighbors.getValue(currentDirection)
                        nextPos = newArea.movePositionFromAtoB(currentPosition)
                        nextDirection = newArea.getDirectionFromAtoB()
                        nextArea = newArea.areaB
                    }

                    var nextTile = map.getValue(nextArea.start + nextPos)


                    if (nextTile == Tile.WALL) {
                        break;
                    }
                    currentDirection = nextDirection
                    currentPosition = nextPos
                    currentArea = nextArea
                }
                currentDirection = currentDirection.turn(s.direction)
            }

            currentPosition = currentArea.start + currentPosition

            val dirVal = when (currentDirection) {
                Vector2D.RIGHT -> 0
                Vector2D.DOWN -> 1
                Vector2D.LEFT -> 2
                Vector2D.UP -> 3
                else -> throw RuntimeException("")
            }

            return ((currentPosition.y + 1) * 1000 + (currentPosition.x + 1) * 4 + dirVal).toString()
        }

    }

    companion object {

        val SIDE_LENGTH = 50
        val MAX_SIDE = SIDE_LENGTH -1

        data class AdjacentSides(val areaA: Area, val sideA: Vector2D, val areaB: Area, val sideB: Vector2D) {
            fun movePositionFromAtoB(lastOnAreaAPosition: Vector2D): Vector2D {

                val oldCoordinate = if (sideA == Vector2D.LEFT || sideA == Vector2D.RIGHT) {
                    lastOnAreaAPosition.y
                } else {
                    lastOnAreaAPosition.x
                }

                val sideset = setOf(sideA,sideB)
                val newOldCoordinate =
                    if (sideset.size == 1 || sideset == setOf(Vector2D.RIGHT, Vector2D.UP) || sideset == setOf(Vector2D.LEFT, Vector2D.DOWN)) {
                        MAX_SIDE - oldCoordinate
                    } else {
                    oldCoordinate
                    }


                if (sideB == Vector2D.LEFT) {
                    return Vector2D(0, newOldCoordinate)
                } else if (sideB == Vector2D.UP) {
                    return Vector2D(newOldCoordinate, 0)
                } else if (sideB == Vector2D.RIGHT) {
                    return Vector2D(MAX_SIDE, newOldCoordinate)
                } else if (sideB == Vector2D.DOWN) {
                    return Vector2D(newOldCoordinate, MAX_SIDE)
                } else {
                    throw RuntimeException("error")
                }
            }

            fun getDirectionFromAtoB(): Vector2D {
                return Vector2D.ZERO - sideB
            }
        }
        data class Area(
            val id: Int,
            val start: Vector2D,
            var neighbors: Map<Vector2D, AdjacentSides> = mutableMapOf()
        ) {
            override fun toString(): String {
                return "Area(id=$id, start=$start)"
            }
        }

        enum class Tile {
            VOID,
            PATH,
            WALL;

            companion object {
                fun fromChar(a: Char): Tile {
                    return if (a == ' ') {
                        VOID
                    } else if (a == '.') {
                        PATH
                    } else {
                        WALL
                    }
                }
            }

        }


        data class Step(val count: Int, val direction: Char)
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

            fun turn(direction: Char): Vector2D {
                return when (direction) {
                    'R' -> when (this) {
                        UP -> RIGHT
                        RIGHT -> DOWN
                        DOWN -> LEFT
                        LEFT -> UP
                        else -> throw RuntimeException("")
                    }

                    'L' -> when (this) {
                        UP -> LEFT
                        LEFT -> DOWN
                        DOWN -> RIGHT
                        RIGHT -> UP
                        else -> throw RuntimeException("")
                    }

                    'N' -> this

                    else -> throw RuntimeException("")
                }

            }

            companion object {
                val ZERO = Vector2D(0, 0)
                val LEFT = Vector2D(-1, 0)
                val UP = Vector2D(0, -1)
                val RIGHT = Vector2D(1, 0)
                val DOWN = Vector2D(0, 1)
            }
        }
    }
}