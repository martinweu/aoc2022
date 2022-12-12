package puzzles

import Puzzle
import java.util.*

class Puzzle12P1 : Puzzle() {
    override fun solve(lines: List<String>): String {
        return Puzzle12().solve(lines, false)
    }
}


class Puzzle12P2 : Puzzle() {
    override fun solve(lines: List<String>): String {
        return Puzzle12().solve(lines, true)
    }

}

class Puzzle12 {
    fun solve(lines: List<String>, level2: Boolean): String {
        val cellMap = parseCellMap(lines)

        val navigateableMap = createNavigatableMap(cellMap)

        val mapMinimalDistance = mutableMapOf<puzzles.Vector2D, Int>()

        val states = PriorityQueue<GameState>()
        if (level2) {
            cellMap.cellMap.filter { it.value.height == 'a' }.map {
                states.add(GameState(it.key, emptyList()))
            }
        } else {
            states.add(GameState(cellMap.start, emptyList()))
        }

        return shortestPath(states, cellMap.emitter, navigateableMap)
    }

    private fun createNavigatableMap(cellMap: CellMap): Map<Vector2D, CellWithNeighbors> {
        val navigateableMap = mutableMapOf<Vector2D, CellWithNeighbors>()
        cellMap.cellMap.forEach { _, currentCell ->
            val directions = Vector2D.ALL.mapNotNull {
                val n = cellMap.cellMap[currentCell.pos + it]
                if (n == null || n.height > (currentCell.height + 1)) {
                    null
                } else {
                    it to n
                }
            }.toMap()
            navigateableMap[currentCell.pos] = CellWithNeighbors(currentCell, directions)
        }
        return navigateableMap
    }

    private fun parseCellMap(lines: List<String>): CellMap {
        val map = mutableMapOf<Vector2D, Cell>()
        var startP: Vector2D? = null
        var emitterP: Vector2D? = null

        lines.forEachIndexed { y, l ->
            l.forEachIndexed { x, c ->
                val currentPos = Vector2D(x, y)
                val levelc = if (c == 'S') {
                    startP = currentPos
                    'a'
                } else if (c == 'E') {
                    emitterP = currentPos
                    'z'
                } else {
                    c
                }
                map[currentPos] = Cell(currentPos, levelc)
            }
        }
        val start = startP!!
        val emitter = emitterP!!
        return CellMap(startP!!, emitterP!!, map)
    }

    private fun shortestPath(
        states: PriorityQueue<GameState>,
        emitter: Vector2D,
        navigatableMap: Map<Vector2D, CellWithNeighbors>
    ): String {
        val visited = mutableSetOf<Vector2D>()
        var costs = -1
        while (true) {
            val first = states.first()

            if (first.pos == emitter) {
                costs = first.path.size
                break;
            }

            states.remove(first)
            for ((k, n) in navigatableMap.getValue(first.pos).directionToNeighbor) {
                if (!visited.contains(n.pos)) {
                    states.add(GameState(n.pos, first.path + k))
                    visited.add(n.pos)
                }
            }
        }
        return costs.toString()
    }

    class GameState(val pos: Vector2D, val path: List<Vector2D>) :
        Comparable<GameState> {
        override fun compareTo(other: GameState): Int {
            return path.size - other.path.size
        }
    }

    data class CellMap(val start: Vector2D, val emitter: Vector2D, val cellMap: Map<Vector2D, Cell>)

    data class CellWithNeighbors(val cell: Cell, val directionToNeighbor: Map<Vector2D, Cell>)
    data class Cell(val pos: Vector2D, val height: Char)
    data class Vector2D(val x: Int, val y: Int) {
        operator fun plus(b: Vector2D): Vector2D {
            return Vector2D(x + b.x, y + b.y)
        }

        companion object {
            val LEFT = Vector2D(-1, 0)
            val UP = Vector2D(0, -1)
            val RIGHT = Vector2D(1, 0)
            val DOWN = Vector2D(0, 1)

            val ALL = listOf(LEFT, UP, RIGHT, DOWN)
        }
    }
}


