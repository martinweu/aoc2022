package puzzles

import Puzzle

class Puzzle11P1 : Puzzle() {
    override fun solve(lines: List<String>): String {
        val monkeyStarts = parseMonkeys(lines)

        var currentItems =
            monkeyStarts.associate { it.index to it.items.map { it.toLong() }.toMutableList() }.toMutableMap()

        var round = 0
        var inspectionCounts = monkeyStarts.associate { it.index to 0L }.toMutableMap()

        while (round < 20) {
            for (m in monkeyStarts) {
                val currentMonkeyItems = currentItems.getValue(m.index)
                currentItems[m.index] = mutableListOf()

                for (item in currentMonkeyItems) {
                    inspectionCounts[m.index] = inspectionCounts.getValue(m.index) + 1
                    val newItem = m.newItem(item, null,3)
                    val newDest = m.getDest(newItem)
                    val destNewItems = currentItems.getValue(newDest)
                    destNewItems.add(newItem)
                }
            }
            round++
        }

        val topMonkeys = inspectionCounts.values.sorted().takeLast(2)
        val business = topMonkeys.reduce(Long::times)
        return business.toString()
    }
}

private fun parseMonkeys(lines: List<String>): List<MonkeyStart> {
    var lineIndex = 0
    val monkeyStarts = mutableListOf<MonkeyStart>()
    while (lineIndex < lines.size) {
        val currentMonkey = MonkeyStart(
            lines[lineIndex++].substring("Monkey ".length).trim(':').toInt(),
            lines[lineIndex++].substring("  Starting items: ".length).split(", ").map { it.toInt() },
            lines[lineIndex].substring("  Operation: new = old ".length).let {
                if (it.startsWith("* old")) {
                    2
                } else {
                    1
                }
            },
            lines[lineIndex].substring("  Operation: new = old ".length).let {
                if (!it.startsWith("* old") && it[0] == '*') {
                    it.substring(2).toInt()
                } else {
                    1
                }
            },
            lines[lineIndex++].substring("  Operation: new = old ".length).let {
                if (it[0] == '+') {
                    it.substring(2).toInt()
                } else {
                    0
                }
            },
            lines[lineIndex++].substring("  Test: divisible by ".length).toInt(),
            lines[lineIndex++].substring("    If true: throw to monkey ".length).toInt(),
            lines[lineIndex++].substring("    If false: throw to monkey ".length).toInt(),
        )
        monkeyStarts.add(currentMonkey)
        lineIndex++
    }
    return monkeyStarts
}

data class MonkeyStart(
    val index: Int,
    val items: List<Int>,
    val power: Int,
    val mul: Int,
    val add: Int,
    val divTest: Int,
    val trueDest: Int,
    val falseDest: Int
) {
    fun newItem(item: Long, normalize: Long? = null, divider : Long): Long {
        val mul1 = if (power == 1) {
            mul.toLong()
        } else {
            item
        }
        return if (normalize != null) {
            (item * mul1 + add) / divider % normalize
        } else {
            (item * mul1 + add) / divider
        }
    }

    fun getDest(item: Long): Int {

        return if (item % divTest == 0L) {
            trueDest
        } else {
            falseDest
        }
    }
}

class Puzzle11P2 : Puzzle() {
    override fun solve(lines: List<String>): String {
        val monkeyStarts = parseMonkeys(lines)

        val normalizer = monkeyStarts.map { it.divTest.toLong() }.reduce(Long::times)

        var currentItems =
            monkeyStarts.associate { it.index to it.items.map { it.toLong() }.toMutableList() }.toMutableMap()

        var round = 0
        var inspectionCounts = monkeyStarts.associate { it.index to 0L }.toMutableMap()

        while (round < 10000) {
            for (m in monkeyStarts) {
                val currentMonkeyItems = currentItems.getValue(m.index)
                currentItems[m.index] = mutableListOf()

                for (item in currentMonkeyItems) {
                    inspectionCounts[m.index] = inspectionCounts.getValue(m.index) + 1L
                    val newItem = m.newItem(item, normalizer, 1)
                    val newDest = m.getDest(newItem)
                    val destNewItems = currentItems.getValue(newDest)
                    destNewItems.add(newItem)
                }
            }
            round++
        }

        val topMonkeys = inspectionCounts.values.sorted().takeLast(2)
        val business = topMonkeys.reduce(Long::times)
        return business.toString()
    }

}