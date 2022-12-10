package puzzles

import Puzzle

class Puzzle10P1 : Puzzle() {
    override fun solve(lines: List<String>): String {
        var currentRegisterX = 1L
        var registerXOverTime = mutableListOf<Long>()
        for (line in lines) {
            if (line == "noop") {
                registerXOverTime.add(currentRegisterX)
            } else {
                val added = line.split(" ")[1].toLong()
                registerXOverTime.add(currentRegisterX)
                registerXOverTime.add(currentRegisterX)
                currentRegisterX += added
            }
        }
        registerXOverTime.add(currentRegisterX)

        var sum = 0L
        var index = 19
        while (index < registerXOverTime.size) {
            sum += registerXOverTime[index] * (index + 1)
            index += 40

        }
        return sum.toString()
    }
}

class Puzzle10P2 : Puzzle() {
    override fun solve(lines: List<String>): String {
        var currentRegisterX = 1L
        var registerXOverTime = mutableListOf<Long>()
        for (line in lines) {
            if (line == "noop") {
                registerXOverTime.add(currentRegisterX)
            } else {
                val added = line.split(" ")[1].toLong()
                registerXOverTime.add(currentRegisterX)
                registerXOverTime.add(currentRegisterX)
                currentRegisterX += added
            }
        }
        registerXOverTime.add(currentRegisterX)

        var output = ""

        for (i in 0L until registerXOverTime.size) {
            val screenPos = i%40
            val reg = registerXOverTime[i.toInt()]
            if (reg == screenPos - 1 || reg == screenPos || reg == screenPos + 1) {
                output += '#'
            } else {
                output += "."
            }
        }

        val matrix = output.windowed(40,40).joinToString ("\n")
        println(matrix)
        return matrix
    }

}