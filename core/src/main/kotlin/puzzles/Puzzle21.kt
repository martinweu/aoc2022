package puzzles

import Puzzle
import java.util.stream.LongStream.range

class Puzzle21 {
    class Part1 : Puzzle() {
        override fun solve(lines: List<String>): String {
            val monkeyRecords = mutableListOf<MonkeyRecord>()
            for (l in lines) {
                val (id, equation) = l.split(": ")
                val m = if (!equation.contains(" ")) {
                    MonkeyRecord(id, equation.toInt())
                } else {
                    val (a, operation, b) = equation.split(" ")
                    MonkeyRecord(id, null, a, operation, b)
                }
                monkeyRecords.add(m)
            }
            monkeyRecords.associateBy { it.id }

            val monkeys = monkeyRecords.map { Monkey(it.id, it.number?.toLong()) }.associateBy { it.id }
            for (mr in monkeyRecords) {
                if (mr.number == null) {
                    val m = monkeys.getValue(mr.id)
                    m.a = monkeys.getValue(mr.a!!)
                    m.operation = mr.operation
                    m.b = monkeys.getValue(mr.b!!)
                }
            }
            return monkeys.getValue("root").compute().toString()
        }
    }

    class Part2 : Puzzle() {
        override fun solve(lines: List<String>): String {
            val monkeyRecords = mutableListOf<MonkeyRecord>()
            for (l in lines) {
                val (id, equation) = l.split(": ")
                val m = if (!equation.contains(" ")) {
                    MonkeyRecord(id, equation.toInt())
                } else {
                    val (a, operation, b) = equation.split(" ")
                    MonkeyRecord(id, null, a, operation, b)
                }
                monkeyRecords.add(m)
            }
            monkeyRecords.associateBy { it.id }

            val monkeys = monkeyRecords.map { Monkey(it.id, it.number?.toLong()) }.associateBy { it.id }
            for (mr in monkeyRecords) {
                if (mr.number == null) {
                    val m = monkeys.getValue(mr.id)
                    m.a = monkeys.getValue(mr.a!!)
                    m.operation = mr.operation
                    m.b = monkeys.getValue(mr.b!!)
                }
            }
            val humn = monkeys.getValue("humn")
            humn.number = null

            val root = monkeys.getValue("root")

            var resultA: Long? = null
            try {
                resultA = root.a!!.compute()
            } catch (e: Exception) {
            }
            var resultB: Long? = null
            try {
                resultB = root.b!!.compute()
            } catch (e: Exception) {
            }

            val target = resultA ?: resultB
            val monkeyPath = if (resultA == null) {
                root.a!!
            } else {
                root.b!!
            }

            val possibleTargets = monkeyPath.findHuman(setOf(target!!))

            for (i in possibleTargets) {
                humn.number = i
                if (monkeyPath.compute() == target) {
                    return i.toString()
                }
            }

            return "error"
        }

    }

    companion object {

        data class Monkey(
            val id: String,
            var number: Long? = null,
            var a: Monkey? = null,
            var operation: String? = null,
            var b: Monkey? = null
        ) {
            fun compute(): Long {
                return if (number != null) {
                    number!!.toLong()
                } else {
                    check(a != null)
                    check(b != null)

                    val ar = a!!.compute()
                    val br = if (a === b) {
                        ar
                    } else if (ar == 0L && operation == "*") {
                        0
                    } else {
                        b!!.compute()
                    }

                    when (operation) {
                        "+" -> ar + br
                        "-" -> ar - br
                        "*" -> ar * br
                        "/" -> ar / br
                        else -> throw RuntimeException("invalid operation")
                    }
                }
            }

            fun findHuman(targets: Set<Long>): Set<Long> {

                if (id == "humn") {
                    return targets;
                }

                var resultA: Long? = null
                try {
                    resultA = a!!.compute()
                } catch (e: Exception) {
                }
                var resultB: Long? = null
                try {
                    resultB = b!!.compute()
                } catch (e: Exception) {
                }
                val whatEverResult = resultA ?: resultB

                checkNotNull(whatEverResult) {
                    "Whatever is null"
                }

                val monkeyPath = if (resultA == null) {
                    a!!
                } else {
                    b!!
                }

                val newTargets = when (operation) {
                    "+" -> targets.map { it - whatEverResult!! }
                    "-" -> if (resultA == null) {
                        targets.map { it + resultB!! }
                    } else {
                        targets.map { resultA!! - it }
                    }

                    "*" -> targets.map { it / whatEverResult!! }
                    "/" -> if (resultA == null) {
                        targets.map { it * resultB!! }
//                        targets.flatMap { LongRange(it * resultB!!, (it * (resultB!!+1))-1).toSet() }
                    } else {
                        targets.map { resultA / it }
                    }

                    else -> throw RuntimeException("invalid operation")
                }
                return monkeyPath.findHuman(newTargets.toSet())
            }
        }

        data class MonkeyRecord(
            val id: String,
            val number: Int? = null,
            val a: String? = null,
            val operation: String? = null,
            val b: String? = null
        )
    }
}