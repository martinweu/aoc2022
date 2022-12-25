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

            var minimumY = map.filterValues { it == Tile.PATH }.keys.minOf { it.y }

            var currentPosition = map.filterValues { it == Tile.PATH }.filterKeys { it.y == minimumY }.keys.minBy { it.x }
            var currentDirection = Vector2D.RIGHT

            val areas = mapOf<Int, Area>(
                1 to Area(Vector2D(2 * 50,0 * 50)),
                2 to Area(Vector2D(1 * 50,0 * 50)),
                3 to Area(Vector2D(1 * 50,1 * 50)),
                4 to Area(Vector2D(1 * 50,2 * 50)),
                5 to Area(Vector2D(0 * 50,2 * 50)),
                6 to Area(Vector2D(0 * 50,3 * 50))
            )

            areas.getValue(1).neighbors = mapOf(
                Matrix2D.ONE to areas.getValue(6),
                Matrix2D.RIGHT_TO_RIGHT to areas.getValue(4),
                Matrix2D.BOTTOM_TO_RIGHT to areas.getValue(3),
            )
            areas.getValue(2).neighbors = mapOf(
               // Matrix2D.SWITCH_Y to areas.getValue(5),
                Matrix2D.RIGHT_TO_RIGHT to areas.getValue(4),
                Matrix2D.BOTTOM_TO_RIGHT to areas.getValue(3),
            )



            for (s in steps) {
                for (i in 0 until s.count) {
                    var nextPos = currentPosition + currentDirection
                    var nextTile = map.getOrDefault(nextPos, Tile.VOID)

                    if (nextTile == Tile.VOID) {

                        if(100 <= currentPosition.x && currentPosition.x < 150 && 0 <= currentPosition.y && currentPosition.y< 100)
                        {

                            //Tile 1 Bottom
                            if(nextPos.y < 0){
                                nextPos = Vector2D( 50 - nextPos.x - 2 * 50, nextPos.y + 4 * 50 )
                            }
                            //Tile 1 Bottom
                            if(nextPos.x > 150 ){
                                nextPos = Vector2D( 50 - nextPos.x - 2 * 50, 150 - nextPos.y)
                            }
                        }

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

    companion object {

        data class Area(val start : Vector2D,var neighbors: Map<Matrix2D, Area> = emptyMap())
        {
            fun isOnArea(v: Vector2D)
            {
             //TODO
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

        data class Matrix2D(val a : Int, val b : Int, val c : Int, val d: Int)  {
            operator fun times(v: Vector2D): Vector2D {
                return Vector2D(a * v.x + b * v.y,c * v.x + d * v.y)
            }
            operator fun times(v: Matrix2D): Matrix2D {
                return Matrix2D(a * v.a + b * v.c, a*v.b + b * v.d, c*v.a + d * v.c, c * v.b + d * v.d)
            }

            companion object
            {
                val ONE = Matrix2D(1,0,0,1)
//                val SWITCH_X = Matrix2D(-1,0,0,1)
//                val SWITCH_Y = Matrix2D(1,0,0,-1)

                val ROTATE_RIGHT = Matrix2D(0,1,-1,0)
                val ROTATE_LEFT = Matrix2D(0,-1,1,0)
                val ROTATE_180 = ROTATE_LEFT  * ROTATE_LEFT


                val LEFT_TO_LEFT = ROTATE_180
                val RIGHT_TO_RIGHT = ROTATE_180

                val RIGHT_TO_TOP = ROTATE_RIGHT
                val RIGHT_TO_BOTTOM = ROTATE_LEFT

                val LEFT_TO_TOP = ROTATE_LEFT
                val LEFT_TO_BOTTOM = ROTATE_RIGHT

                val BOTTOM_TO_RIGHT = ROTATE_RIGHT
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
                val LEFT = Vector2D(-1, 0)
                val UP = Vector2D(0, -1)
                val RIGHT = Vector2D(1, 0)
                val DOWN = Vector2D(0, 1)
            }
        }
    }
}