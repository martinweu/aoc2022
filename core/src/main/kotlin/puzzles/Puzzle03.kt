package puzzles

import Puzzle
import java.util.stream.IntStream.range

class Puzzle03P1 : Puzzle() {
    override fun solve(lines: List<String>): String {

        val priorities = mutableListOf<Int>()

        for (line in lines) {
            val startIndexSecondPart = line.length / 2
            val firstPart = line.substring(0, startIndexSecondPart)
            val secondPart = line.substring(startIndexSecondPart)

            val interSect = firstPart.toSet().intersect(secondPart.toSet())
            check(interSect.size == 1) {
                throw RuntimeException("No intersection found")
            }
            val commonItem = interSect.first()
            val priority = if ('A' <= commonItem && commonItem <= 'Z') {
                commonItem - 'A' + 27
            } else if ('a' <= commonItem && commonItem <= 'z') {
                commonItem - 'a' + 1
            } else {
                throw RuntimeException("Invalid character found")
            }
            priorities.add(priority)
        }
        return priorities.sum().toString()
    }
}

class Puzzle03P2 : Puzzle() {
    override fun solve(lines: List<String>): String {

        val priorities = mutableListOf<Int>()

        for (group in range(0, lines.size / 3)) {

            val badges =
                lines[group * 3].toSet()
                    .intersect(lines[group * 3 + 1].toSet())
                    .intersect(lines[group * 3 + 2].toSet())

            check(badges.size == 1)
            {
                "Badges are not unique in group $group"
            }
            val badge = badges.first()
            val priority = if ('A' <= badge && badge <= 'Z') {
                badge - 'A' + 27
            } else if ('a' <= badge && badge <= 'z') {
                badge - 'a' + 1
            } else {
                throw RuntimeException("Invalid character found")
            }
            priorities.add(priority)
        }
        return priorities.sum().toString()
    }
}