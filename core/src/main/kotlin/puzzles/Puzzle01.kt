package puzzles

import Puzzle

class Puzzle01P1 : Puzzle() {
    override fun solve(lines: List<String>): String {

        var currentElf = 0
        var maxElf = -1
        for (line in lines) {
            if (line.isBlank()) {
                if (currentElf > maxElf) {
                    maxElf = currentElf
                }
                currentElf = 0
            } else {
                currentElf += line.toInt()
            }
        }
        return maxElf.toString()
    }
}

class Puzzle01P2 : Puzzle() {
    override fun solve(lines: List<String>): String {

        val elvesCalories = mutableListOf<Int>()

        var currentElve = 0

        for (line in lines) {
            if (line.isBlank()) {
                elvesCalories.add(currentElve)
                currentElve = 0
            } else {
                currentElve += line.toInt()
            }
        }
        elvesCalories.add(currentElve)

        return elvesCalories.sortedDescending().take(3).sum().toString()
    }

}