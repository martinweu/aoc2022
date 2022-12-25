package puzzles

import Puzzle
import java.util.PriorityQueue

class Puzzle24 {
    class Part1 : Puzzle() {
        override fun solve(lines: List<String>): String {
            val blizzardsUp = mutableMapOf<Int, MutableSet<Int>>()
            val blizzardsDown = mutableMapOf<Int, MutableSet<Int>>()
            val blizzardsLeft = mutableMapOf<Int, MutableSet<Int>>()
            val blizzardsRight = mutableMapOf<Int, MutableSet<Int>>()
            val walls = mutableSetOf<Vector2D>()

            lines.forEachIndexed { y, s ->
                s.forEachIndexed { x, c ->
                    when (c) {
                        '^' -> blizzardsUp.computeIfAbsent(x) { mutableSetOf() }.add(y)
                        'v' -> blizzardsDown.computeIfAbsent(x) { mutableSetOf() }.add(y)
                        '<' -> blizzardsLeft.computeIfAbsent(y) { mutableSetOf() }.add(x)
                        '>' -> blizzardsRight.computeIfAbsent(y) { mutableSetOf() }.add(x)
                        '#' -> walls.add(Vector2D(x, y))
                    }
                }
            }
            val start = Vector2D(lines.first().indexOf('.'), 0)
            val target = Vector2D(lines.last().indexOf('.'), lines.size - 1)

            val width = lines.first().length - 2
            val height = lines.size - 2

            val queue = PriorityQueue<State>()
            val visited = mutableSetOf<State>()
            val initialState = State(start, (target - start).manhattan(), 0)
            queue.add(initialState)
            visited.add(initialState)

            var current = initialState
            while (current.pos != target) {
                val newTime = current.time + 1
                for (d in Vector2D.allDirections) {
                    val newPos = current.pos + d
                    if (walls.contains(newPos) || newPos.y < 0 || newPos.x < 0 || newPos.x > (width + 1) || newPos.y > (height + 1)) {
                        continue;
                    }
                    val containsBlizzard =
                        blizzardsUp[newPos.x].orEmpty()
                            .contains(((newPos.y + (newTime % height) - 1 + height) % height) + 1)
                                || blizzardsDown[newPos.x].orEmpty()
                            .contains(((newPos.y - (newTime % height) - 1 + height) % height) + 1)
                                || blizzardsLeft[newPos.y].orEmpty()
                            .contains(((newPos.x + (newTime % width) - 1 + width) % width) + 1)
                                || blizzardsRight[newPos.y].orEmpty()
                            .contains(((newPos.x - (newTime % width) - 1 + width) % width) + 1)
                    if (!containsBlizzard) {
                        val newState = State(newPos, (target - newPos).manhattan(), newTime)
                        if (!visited.contains(newState)) {
                            visited.add(newState)
                            queue.add(newState)
                        }
                    }
                }
                current = queue.first()
                queue.remove(current)
            }
            val time = current.time
            return time.toString()
        }
    }

    class Part2 : Puzzle() {
        override fun solve(lines: List<String>): String {
            val blizzardsUp = mutableMapOf<Int, MutableSet<Int>>()
            val blizzardsDown = mutableMapOf<Int, MutableSet<Int>>()
            val blizzardsLeft = mutableMapOf<Int, MutableSet<Int>>()
            val blizzardsRight = mutableMapOf<Int, MutableSet<Int>>()
            val walls = mutableSetOf<Vector2D>()

            lines.forEachIndexed { y, s ->
                s.forEachIndexed { x, c ->
                    when (c) {
                        '^' -> blizzardsUp.computeIfAbsent(x) { mutableSetOf() }.add(y)
                        'v' -> blizzardsDown.computeIfAbsent(x) { mutableSetOf() }.add(y)
                        '<' -> blizzardsLeft.computeIfAbsent(y) { mutableSetOf() }.add(x)
                        '>' -> blizzardsRight.computeIfAbsent(y) { mutableSetOf() }.add(x)
                        '#' -> walls.add(Vector2D(x, y))
                    }
                }
            }
            val start = Vector2D(lines.first().indexOf('.'), 0)
            val target = Vector2D(lines.last().indexOf('.'), lines.size - 1)

            val width = lines.first().length - 2
            val height = lines.size - 2
            val startTime = 0
            val timeToTarget0 = shortestPath(
                start,
                target,
                startTime,
                walls,
                width,
                height,
                blizzardsUp,
                blizzardsDown,
                blizzardsLeft,
                blizzardsRight
            )
            val timeBackToStart = shortestPath(
                target,
                start,
                timeToTarget0,
                walls,
                width,
                height,
                blizzardsUp,
                blizzardsDown,
                blizzardsLeft,
                blizzardsRight
            )
            val timeToTarget1 = shortestPath(
                start,
                target,
                timeBackToStart,
                walls,
                width,
                height,
                blizzardsUp,
                blizzardsDown,
                blizzardsLeft,
                blizzardsRight
            )
            return timeToTarget1.toString()
        }

        private fun shortestPath(
            start: Vector2D,
            target: Vector2D,
            startTime: Int,
            walls: MutableSet<Vector2D>,
            width: Int,
            height: Int,
            blizzardsUp: MutableMap<Int, MutableSet<Int>>,
            blizzardsDown: MutableMap<Int, MutableSet<Int>>,
            blizzardsLeft: MutableMap<Int, MutableSet<Int>>,
            blizzardsRight: MutableMap<Int, MutableSet<Int>>
        ): Int {
            val queue = PriorityQueue<State>()
            val visited = mutableSetOf<State>()
            val initialState = State(start, (target - start).manhattan(), startTime)
            queue.add(initialState)
            visited.add(initialState)

            var current = initialState
            while (current.pos != target) {
                val newTime = current.time + 1
                for (d in Vector2D.allDirections) {
                    val newPos = current.pos + d
                    if (walls.contains(newPos) || newPos.y < 0 || newPos.x < 0 || newPos.x > (width + 1) || newPos.y > (height + 1)) {
                        continue;
                    }
                    val containsBlizzard =
                        blizzardsUp[newPos.x].orEmpty()
                            .contains(((newPos.y + (newTime % height) - 1 + height) % height) + 1)
                                || blizzardsDown[newPos.x].orEmpty()
                            .contains(((newPos.y - (newTime % height) - 1 + height) % height) + 1)
                                || blizzardsLeft[newPos.y].orEmpty()
                            .contains(((newPos.x + (newTime % width) - 1 + width) % width) + 1)
                                || blizzardsRight[newPos.y].orEmpty()
                            .contains(((newPos.x - (newTime % width) - 1 + width) % width) + 1)
                    if (!containsBlizzard) {
                        val newState = State(newPos, (target - newPos).manhattan(), newTime)
                        if (!visited.contains(newState)) {
                            visited.add(newState)
                            queue.add(newState)
                        }
                    }
                }
                current = queue.first()
                queue.remove(current)
            }
            val time = current.time
            return time
        }

    }

    companion object {
        data class State(val pos: Vector2D, val distance: Int, val time: Int) : Comparable<State> {
            override fun compareTo(other: State): Int {
                return (time + distance).compareTo(other.time + other.distance)
            }
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

            fun manhattan(): Int {
                return Math.abs(x) + Math.abs(y)
            }

            companion object {
                val ZERO = Vector2D(0, 0)
                val LEFT = Vector2D(-1, 0)
                val UP = Vector2D(0, -1)
                val RIGHT = Vector2D(1, 0)
                val DOWN = Vector2D(0, 1)

                val allDirections = setOf(ZERO, LEFT, UP, RIGHT, DOWN)
            }
        }
    }
}