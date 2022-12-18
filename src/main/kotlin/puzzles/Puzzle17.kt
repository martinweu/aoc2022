package puzzles

import Puzzle

class Puzzle17 {
    class Part1 : Puzzle() {
        override fun solve(lines: List<String>): String {
            val width = 7
            val startRight = 2L
            val startAbove = 3L
            val jets = lines.first()
            var currentShape = 0
            var currentRock = Rock(0, Vector2D(startRight, startAbove), Shape.HBAR)
            val stoppedRocks = mutableListOf<Rock>()
            var maxRockHeight = 0L
            var jetIndex = 0

            while (currentRock.id < 2022) {
                val jet = jets[jetIndex]
                jetIndex = (jetIndex + 1) % jets.length

//                if (jetIndex == 0) {
//                    println("loop")
//                }

                val jettedRock = currentRock.leftBottom + Vector2D.fromJet(jet)
                val canJet =
                    jettedRock.x >= 0 && currentRock.shape.getMaxWidth(jettedRock) <= width && !stoppedRocks.any {
                        it.intersects(
                            jettedRock,
                            currentRock.shape
                        )
                    }

                if (canJet) {
                    currentRock.leftBottom = jettedRock
                }

                val fallenRock = currentRock.leftBottom + Vector2D.DOWN
                val canFall = fallenRock.y >= 0 && !stoppedRocks.any { it.intersects(fallenRock, currentRock.shape) }

                if (canFall) {
                    currentRock.leftBottom = fallenRock
                } else {
                    stoppedRocks.add(currentRock)
                    currentShape = (currentShape + 1) % Shape.values().size;
                    maxRockHeight = Math.max(maxRockHeight, currentRock.getMaxHeight())
                    val newRockStart = maxRockHeight + startAbove
                    currentRock =
                        Rock(currentRock.id + 1, Vector2D(startRight, newRockStart), Shape.values()[currentShape])
                }
            }

            for (y in maxRockHeight downTo 0) {
                var str = ""
                for (x in 0L until 7) {
                    val v = Vector2D(x, y)
                    if (currentRock.getTiles().contains(v)) {
                        str += "@"
                    } else if (stoppedRocks.any { it.getTiles().contains(v) }) {
                        str += "#"
                    } else {
                        str += "."
                    }
                }
                println(str)
            }
            println()

            return maxRockHeight.toString()
        }

        data class Rock(var id: Long, var leftBottom: Vector2D, var shape: Shape) {
            fun getTiles(): Set<Vector2D> {
                return shape.getTiles(leftBottom)
            }

            fun intersects(leftBottom: Vector2D, shape: Shape): Boolean {

                if (Math.abs(leftBottom.y - this.leftBottom.y) > 4) {
                    return false
                }

                return this.getTiles().intersect(shape.getTiles(leftBottom)).isNotEmpty()
            }

            fun getMaxHeight(): Long {
                return shape.getMaxHeight(leftBottom)
            }

            fun getMaxWidth(): Long {
                return shape.getMaxWidth(leftBottom)
            }
        }

    }

    enum class Shape {
        HBAR, PLUS, L, VBAR, BOX;

        fun getNext(): Shape {
            return Shape.values()[(this.ordinal + 1) % Shape.values().size]
        }

        fun getMaxHeight(leftBottom: Vector2D): Long {
            val y = leftBottom.y
            return when (this) {
                HBAR -> y + 1
                PLUS, L -> y + 3
                VBAR -> y + 4
                BOX -> y + 2
            }
        }

        fun getMaxWidth(leftBottom: Vector2D): Long {
            val x = leftBottom.x
            return when (this) {
                HBAR -> x + 4
                PLUS, L -> x + 3
                VBAR -> x + 1
                BOX -> x + 2
            }
        }

        fun getTiles(leftBottom: Vector2D): Set<Vector2D> {
            return when (this) {
                HBAR -> setOf(
                    leftBottom,
                    leftBottom + Vector2D.RIGHT,
                    leftBottom + Vector2D.RIGHT * 2,
                    leftBottom + Vector2D.RIGHT * 3
                )

                PLUS -> setOf(
                    leftBottom + Vector2D.RIGHT,
                    leftBottom + Vector2D.UP,
                    leftBottom + Vector2D.UP + Vector2D.RIGHT,
                    leftBottom + Vector2D.UP + Vector2D.RIGHT * 2,
                    leftBottom + Vector2D.UP * 2 + Vector2D.RIGHT,
                )

                L -> setOf(
                    leftBottom,
                    leftBottom + Vector2D.RIGHT,
                    leftBottom + Vector2D.RIGHT * 2,
                    leftBottom + Vector2D.RIGHT * 2 + Vector2D.UP,
                    leftBottom + Vector2D.RIGHT * 2 + Vector2D.UP * 2,
                )

                VBAR -> setOf(
                    leftBottom,
                    leftBottom + Vector2D.UP,
                    leftBottom + Vector2D.UP * 2,
                    leftBottom + Vector2D.UP * 3,
                )

                BOX -> setOf(
                    leftBottom,
                    leftBottom + Vector2D.RIGHT,
                    leftBottom + Vector2D.UP,
                    leftBottom + Vector2D.UP + Vector2D.RIGHT,
                )
            }
        }
    }


    data class Vector2D(val x: Long, val y: Long) {
        operator fun plus(b: Vector2D): Vector2D {
            return Vector2D(x + b.x, y + b.y)
        }

        operator fun times(b: Int): Vector2D {
            return Vector2D(x * b, y * b)
        }

        operator fun minus(b: Vector2D): Vector2D {
            return Vector2D(x - b.x, y - b.y)
        }

        companion object {
            val LEFT = Vector2D(-1, 0)
            val UP = Vector2D(0, 1)
            val RIGHT = Vector2D(1, 0)
            val DOWN = Vector2D(0, -1)

            fun fromJet(jet: Char): Vector2D {
                return if (jet == '<') {
                    LEFT
                } else {
                    RIGHT
                }
            }
        }
    }

    class Part2 : Puzzle() {

        val WIDTH = 7
        val START_RIGHT = 2L
        val START_ABOVE = 3L

        val STOP_AT = 2022L
//        val STOP_AT = 1000000000000L

        data class JetRound(
            val currentRockPosition: Vector2D,
            val currentRockShape: Shape,
            val stoppedTiles: Set<Vector2D>,
            val removedFloors: Long,
            val stoppedRocksCount: Long,
        )

        override fun solve(lines: List<String>): String {
            val jets = lines.first()

            var currentRound = JetRound(Vector2D(START_RIGHT, START_ABOVE), Shape.HBAR, setOf<Vector2D>(), 0, 0)

            var totalStoppedRocksCount = 0L
            var totalFloors = 0L
            var run = true
            var globalTop: Long? = null

            while (run) {
                var stoppedRocksCount = 0L
                var currentRockPosition = currentRound.currentRockPosition
                var currentShape = currentRound.currentRockShape
                val stoppedTiles = currentRound.stoppedTiles.toMutableSet()
                for (jet in jets) {
                    val jetTo = currentRockPosition + Vector2D.fromJet(jet)
                    val canJet = jetTo.x >= 0 && currentShape.getMaxWidth(jetTo) <= WIDTH &&
                            !currentShape.getTiles(jetTo).any { stoppedTiles.contains(it) }

                    if (canJet) {
                        currentRockPosition = jetTo
                    }

                    val fallenTo = currentRockPosition + Vector2D.DOWN
                    val canFall = fallenTo.y >= 0 && !currentShape.getTiles(fallenTo).any { stoppedTiles.contains(it) }

                    if (canFall) {
                        currentRockPosition = fallenTo
                    } else {
                        stoppedTiles.addAll(currentShape.getTiles(currentRockPosition))
                        stoppedRocksCount++
                        val top = stoppedTiles.maxOf { it.y } + 1
                        if (stoppedRocksCount + totalStoppedRocksCount == STOP_AT) {
                            globalTop = top
                            run = false;
                            break;
                        }
                        currentShape = currentShape.getNext()
                        currentRockPosition = Vector2D(START_RIGHT, top + START_ABOVE)
                    }
                }

                val highestClosedFloor = stoppedTiles
                    //.flatMap { it.getTiles() }
                    .groupBy { it.y }.toSortedMap().values.windowed(2, 1)
                    .filter { it.flatMap { it.map { it.x } }.toSet().size == WIDTH }
                    .maxOfOrNull { it.minOfOrNull { it.minOfOrNull { it.y } ?: 0L } ?: 0L } ?: 0L

                currentRound = JetRound(currentRockPosition - Vector2D(0, highestClosedFloor),
                    currentShape,
                    stoppedTiles.filter { it.y >= highestClosedFloor }.map { Vector2D(it.x, it.y - highestClosedFloor) }.toSet(),
                    highestClosedFloor,
                    stoppedRocksCount
                )

                totalStoppedRocksCount += stoppedRocksCount
                if(run) {
                    totalFloors += highestClosedFloor
                }
            }
            return (totalFloors + globalTop!!).toString()
        }

        data class Rock(var leftBottom: Vector2D, var shape: Shape) {
            fun getTiles(): Set<Vector2D> {
                return shape.getTiles(leftBottom)
            }

            fun intersects(leftBottom: Vector2D, shape: Shape): Boolean {

                if (Math.abs(leftBottom.y - this.leftBottom.y) > 4) {
                    return false
                }

                return this.getTiles().intersect(shape.getTiles(leftBottom)).isNotEmpty()
            }

            fun getMaxHeight(): Long {
                return shape.getMaxHeight(leftBottom)
            }

            fun getMaxWidth(): Long {
                return shape.getMaxWidth(leftBottom)
            }
        }

        data class Cache(val shape: Shape, val currentRock: Vector2D, val stoppedRocks: Set<Vector2D>)

//        override fun solve1(lines: List<String>): String {
//            val width = 7
//            val startRight = 2L
//            val startAbove = 3L
//            val jets = lines.first()
//            var currentShape = 0
//            var currentRock = Rock(0, Vector2D(startRight, startAbove), Shape.HBAR)
//            var stoppedRocks = mutableSetOf<Vector2D>()
////            var maxRockHeight = 0L
//            var jetIndex = 0
//            val totalHeight = 0L
//
//            val cache = mutableMapOf<Cache, Pair<Cache, Long>>()
//
//            var lastCache = Cache(currentRock.shape, currentRock.leftBottom, stoppedRocks)
//            var lastRockId = currentRock.id
//
//            while (currentRock.id < 1000000000000) {
//                val jet = jets[jetIndex]
//                jetIndex = (jetIndex + 1) % jets.length
//
//                if (jetIndex == 0) {
//                    val highestClosedFloor = stoppedRocks
//                        //.flatMap { it.getTiles() }
//                        .groupBy { it.y }.toSortedMap().values.windowed(2, 1)
//                        .filter { it.flatMap { it.map { it.x } }.toSet().size == width }
//                        .maxOfOrNull { it.minOfOrNull { it.minOfOrNull { it.y } ?: 0L } ?: 0L } ?: 0L
//                    val cutBelow = highestClosedFloor
//                    stoppedRocks = stoppedRocks.filter { it.y >= cutBelow }
//                        .map { Vector2D(it.x, it.y - cutBelow) }.toMutableSet()
//                    currentRock.leftBottom = currentRock.leftBottom - Vector2D(0, cutBelow)
//
//                    while (true) {
//                        val currentState = Cache(currentRock.shape, currentRock.leftBottom, stoppedRocks)
//                        if (!cache.contains(lastCache)) {
//                            cache.put(lastCache, Pair(currentState, currentRock.id - lastRockId))
//                        }
//                        val nc = cache[currentState]
//                        if (nc != null) {
//                            currentRock.shape = nc.first.shape
//                            currentRock.leftBottom = nc.first.currentRock
//                            currentRock.id = currentRock.id + nc.second
//                            stoppedRocks = nc.first.stoppedRocks.toMutableSet()
//                            lastRockId = currentRock.id
//                            lastCache = currentState
//                        } else {
//                            lastRockId = currentRock.id
//                            lastCache = currentState
//                            break;
//                        }
//                    }
//
//                    println("rock: ${currentRock.id} cutted at $cutBelow with currentShape $currentShape")
//                }
//
//                val jettedRock = currentRock.leftBottom + Vector2D.fromJet(jet)
//                val canJet =
//                    jettedRock.x >= 0 && currentRock.shape.getMaxWidth(jettedRock) <= width && !currentRock.shape.getTiles(
//                        jettedRock
//                    ).intersect(stoppedRocks).isNotEmpty()
//
//                if (canJet) {
//                    currentRock.leftBottom = jettedRock
//                }
//
//                val fallenRock = currentRock.leftBottom + Vector2D.DOWN
//                val canFall =
//                    fallenRock.y >= 0 && !currentRock.shape.getTiles(fallenRock).intersect(stoppedRocks).isNotEmpty()
//
//                if (canFall) {
//                    currentRock.leftBottom = fallenRock
//                } else {
//                    stoppedRocks.addAll(currentRock.getTiles())
//                    currentShape = (currentShape + 1) % Shape.values().size;
//                    val maxRockHeight = Math.max(stoppedRocks.maxOf { it.y }, currentRock.getMaxHeight())
//                    val newRockStart = maxRockHeight + startAbove
//                    currentRock =
//                        Rock(
//                            currentRock.id + 1,
//                            Vector2D(startRight, newRockStart),
//                            Shape.values()[currentShape]
//                        )
//                }
//            }
//            return maxRockHeight.toString()
//        }

    }
}