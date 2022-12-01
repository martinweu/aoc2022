package puzzles

import Puzzle

class Puzzle00P1 : Puzzle() {
    override fun solve(lines : List<String>): String {
        var counter = -1
        var lastDepth = -1
        for (line in lines) {
            val currentDepth = line.toInt()
            if (currentDepth > lastDepth) {
                counter++
            }
            lastDepth = currentDepth
        }
        return counter.toString()
    }
}

class Puzzle00P2 : Puzzle() {
    override fun solve(lines: List<String>): String {
        TODO("Puzzle 0 Part 2 is not yet implemented")
    }

}