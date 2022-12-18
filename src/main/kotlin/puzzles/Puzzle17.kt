package puzzles

import Puzzle

class Puzzle17 {
    class Part1 : Puzzle() {
        override fun solve(lines: List<String>): String {
            return solve(lines, 2022L)
        }
    }

    class Part2 : Puzzle() {
        override fun solve(lines: List<String>): String {
            return solve(lines, 1000000000000L)
        }
    }

    companion object {
        val WIDTH = 7
        val START_RIGHT = 2L
        val START_ABOVE = 3L

        fun solve(lines: List<String>, rockCount: Long): String {
            val jets = lines.first()

            var currentRound = JetRound(Vector2D(START_RIGHT, START_ABOVE), Shape.HBAR, setOf<Vector2D>(), 0, 0)

            var totalStoppedRocksCount = 0L
            var totalFloors = 0L
            var run = true
            var globalTop: Long? = null
            val cache = mutableMapOf<CacheKey, JetRound>()

            while (run) {
                var stoppedRocksCount = 0L
                var removedFloors = 0L
                val cachedResult = cache[currentRound.getCacheKey()]
                if (cachedResult != null && cachedResult.stoppedRocksCount + totalStoppedRocksCount < rockCount) {
                    val cycle = mutableSetOf<JetRound>()
                    var current = cachedResult
                    while (true) {
                        val a = cache.getValue(current!!.getCacheKey())
                        if (a in cycle) {
                            break;
                        }
                        cycle.add(a)
                        current = a
                    }

                    val cacheCycleStoppedRocks = cycle.sumOf { it.stoppedRocksCount }
                    val cacheCycleRemovedFloors = cycle.sumOf { it.removedFloors }
                    val cycleTimes = (rockCount - totalStoppedRocksCount) / cacheCycleStoppedRocks
                    if (cycleTimes > 0) {
                        stoppedRocksCount = cycleTimes * cacheCycleStoppedRocks
                        removedFloors = cycleTimes * cacheCycleRemovedFloors
                        // do not change currentRound here, only consume cycles
                    } else {
                        stoppedRocksCount = cachedResult.stoppedRocksCount
                        removedFloors = cachedResult.removedFloors
                        currentRound = cachedResult
                    }
                } else {
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
                        val canFall =
                            fallenTo.y >= 0 && !currentShape.getTiles(fallenTo).any { stoppedTiles.contains(it) }

                        if (canFall) {
                            currentRockPosition = fallenTo
                        } else {
                            stoppedTiles.addAll(currentShape.getTiles(currentRockPosition))
                            stoppedRocksCount++
                            val top = stoppedTiles.maxOf { it.y } + 1
                            if (stoppedRocksCount + totalStoppedRocksCount == rockCount) {
                                globalTop = totalFloors + top
                                run = false;
                                break;
                            }
                            currentShape = currentShape.getNext()
                            currentRockPosition = Vector2D(START_RIGHT, top + START_ABOVE)
                        }
                    }

                    removedFloors = stoppedTiles
                        //.flatMap { it.getTiles() }
                        .groupBy { it.y }.toSortedMap().values.windowed(2, 1)
                        .filter { it.flatMap { it.map { it.x } }.toSet().size == WIDTH }
                        .maxOfOrNull { it.minOfOrNull { it.minOfOrNull { it.y } ?: 0L } ?: 0L } ?: 0L

                    val newRound = JetRound(currentRockPosition - Vector2D(0, removedFloors),
                        currentShape,
                        stoppedTiles.filter { it.y >= removedFloors }.map { Vector2D(it.x, it.y - removedFloors) }
                            .toSet(),
                        removedFloors,
                        stoppedRocksCount
                    )
                    cache[currentRound.getCacheKey()] = newRound
                    currentRound = newRound
                }
                totalStoppedRocksCount += stoppedRocksCount
                totalFloors += removedFloors

            }
            return (globalTop).toString()
        }
    }

    data class CacheKey(
        val currentRockPosition: Vector2D,
        val currentRockShape: Shape,
        val stoppedTiles: Set<Vector2D>,
    )

    data class JetRound(
        val currentRockPosition: Vector2D,
        val currentRockShape: Shape,
        val stoppedTiles: Set<Vector2D>,
        val removedFloors: Long,
        val stoppedRocksCount: Long,
    ) {
        fun getCacheKey(): CacheKey {
            return CacheKey(currentRockPosition, currentRockShape, stoppedTiles)
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


}