package puzzles

import Puzzle

class Puzzle05P1 : Puzzle() {
    override fun solve(lines: List<String>): String {

        val regex = Regex("""move (\d+) from (\d+) to (\d+)""")
        val crates = mutableMapOf<Int, ArrayDeque<String>>()
        val moves = mutableListOf<Move>()

        var parseMoves = false

        for (line in lines) {
            if (line.startsWith(" 1 ") || line.isBlank()) {
                parseMoves = true
            }
            else if (!parseMoves) {
                val cratesInLine = line.length / 4 + 1
                //record crates state
                for (i in 0 until cratesInLine) {
                    val crate = line.substring(i * 4, i * 4 + 3).trim()
                    if (crate.isNotBlank()) {
                        val currentStack = crates.getOrPut(i) { ArrayDeque<String>() }
                        currentStack.addLast(crate.substring(1,2))
                    } else {
                        check(!crates.contains(i)) { "Stack $i is created but crate is empty" }
                    }
                }
            } else {
                val parts = regex.find(line)!!.groups.drop(1).map { it!!.value.toInt() }
                moves.add(Move(parts[1] -1, parts[2]-1, parts[0]))
            }
        }
        for (m in moves) {
            val stackFrom = crates.getValue(m.from)
            val stackTo = crates.getValue(m.to)
            for (i in 0 until m.count) {
                stackTo.addFirst(stackFrom.removeFirst())
            }
        }

        var solution = ""
        for(i in 0 until crates.size){
            solution += crates.getValue(i).first()
        }
        return solution
    }
}

data class Move(val from: Int, val to: Int, val count: Int)

class Puzzle05P2 : Puzzle() {
    override fun solve(lines: List<String>): String {

        val regex = Regex("""move (\d+) from (\d+) to (\d+)""")
        val crates = mutableMapOf<Int, ArrayDeque<String>>()
        val moves = mutableListOf<Move>()

        var parseMoves = false

        for (line in lines) {
            if (line.startsWith(" 1 ") || line.isBlank()) {
                parseMoves = true
            }
            else if (!parseMoves) {
                val cratesInLine = line.length / 4 + 1
                //record crates state
                for (i in 0 until cratesInLine) {
                    val crate = line.substring(i * 4, i * 4 + 3).trim()
                    if (crate.isNotBlank()) {
                        val currentStack = crates.getOrPut(i) { ArrayDeque<String>() }
                        currentStack.addLast(crate.substring(1,2))
                    } else {
                        check(!crates.contains(i)) { "Stack $i is created but crate is empty" }
                    }
                }
            } else {
                val parts = regex.find(line)!!.groups.drop(1).map { it!!.value.toInt() }
                moves.add(Move(parts[1] -1, parts[2]-1, parts[0]))
            }
        }
        for (m in moves) {
            val stackFrom = crates.getValue(m.from)
            val stackTo = crates.getValue(m.to)
            val temporary = ArrayDeque<String>()

            for (i in 0 until m.count) {
                temporary.addFirst(stackFrom.removeFirst())
            }
            for (i in 0 until m.count) {
                stackTo.addFirst(temporary.removeFirst())
            }
        }

        var solution = ""
        for(i in 0 until crates.size){
            solution += crates.getValue(i).first()
        }
        return solution
    }
}