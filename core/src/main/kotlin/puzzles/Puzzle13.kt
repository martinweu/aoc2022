package puzzles

import Puzzle

class Puzzle13P1 : Puzzle() {
    override fun solve(lines: List<String>): String {
        val listOfPairs = parsePairs(lines)

        listOfPairs.forEach {
            println(it)
            println(it.check())
            println()
        }

        val sum = listOfPairs.filter { it.check() }.sumOf { it.index }
        return sum.toString()
    }
    private fun parsePairs(lines: List<String>): MutableList<Pair> {
        var lineIndex = 0
        var pairIndex = 1
        val listOfPairs = mutableListOf<Pair>()

        while (lineIndex < lines.size) {
            val a = Puzzle13.Expression.fromString(lines[lineIndex++])
            val b = Puzzle13.Expression.fromString(lines[lineIndex++])
            listOfPairs.add(Pair(pairIndex, a, b))
            pairIndex++
            lineIndex++
        }
        return listOfPairs
    }
    data class Pair(val index: Int, val a: Puzzle13.Expression, val b: Puzzle13.Expression) {
        fun check(): Boolean {
            return a.check(b) <= 0
        }

        override fun toString(): String {
            return a.toString() + "\n" + b.toString()
        }
    }
}


class Puzzle13P2 : Puzzle() {
    override fun solve(lines: List<String>): String {
        val listOfExpressions = parseExpressions(lines)

        val start = Puzzle13.Expression.fromString("[[2]]")
        val end = Puzzle13.Expression.fromString("[[6]]")

        listOfExpressions.add(start)
        listOfExpressions.add(end)

        listOfExpressions.sort()

        val pstart = listOfExpressions.indexOf(start) +1
        val pend = listOfExpressions.indexOf(end) + 1

        val prod = pstart * pend

        return prod.toString()
    }
    private fun parseExpressions(lines: List<String>): MutableList<Puzzle13.Expression> {
        var lineIndex = 0
        val listOfExpressions = mutableListOf<Puzzle13.Expression>()

        while (lineIndex < lines.size) {
            val a = Puzzle13.Expression.fromString(lines[lineIndex++])
            val b = Puzzle13.Expression.fromString(lines[lineIndex++])
            listOfExpressions.add(a)
            listOfExpressions.add(b)
            lineIndex++
        }
        return listOfExpressions
    }

}

class Puzzle13 {

    data class Expression(val element: Int?, val sub: MutableList<Expression>) : Comparable<Expression> {
        val isElement get() = element != null
        val isList get() = element == null

        fun wrap(): Expression {
            check(isElement)
            return Expression(null, sub = mutableListOf(this))
        }

        override fun compareTo(other: Expression): Int {
            return check(other)
        }

        override fun toString(): String {
            if (isElement) {
                return element.toString()
            } else {
                return "[" + sub.map { it.toString() }.joinToString(",") + "]"
            }
        }

        fun check(b: Expression): Int {
            if (this.isElement && b.isElement) {
                return element!! - b.element!!
            }

            val al = if (this.isElement) {
                this.wrap()
            } else {
                this
            }
            val bl = if (b.isElement) {
                b.wrap()
            } else {
                b
            }

            var index = 0
            while (index < al.sub.size && index < bl.sub.size) {
                val checkResult = al.sub[index].check(bl.sub[index])
                if (checkResult != 0) {
                    return checkResult
                }
                index++
            }
            return al.sub.size - bl.sub.size
        }

        companion object {
            fun fromString(str: String): Expression {
                val expressionStack = ArrayDeque<Expression>()

                val root = Expression(null, mutableListOf())
                expressionStack.addFirst(root)

                var numberBuffer = ""

                for (c in str) {
                    if (c == '[') {
                        check(numberBuffer.isBlank()) { "NumberBuffer error with at $c in $str" }
                        expressionStack.addFirst(Expression(null, mutableListOf()))
                    } else if (c == ',') {
                        if (numberBuffer.isNotBlank()) {
                            val currentSub = expressionStack.first()
                            currentSub.sub.add(Expression(numberBuffer.toInt(), mutableListOf()))
                            numberBuffer = ""
                        }
                    } else if (c == ']') {
                        val currentSub = expressionStack.removeFirst()
                        if (numberBuffer.isNotBlank()) {
                            currentSub.sub.add(Expression(numberBuffer.toInt(), mutableListOf()))
                            numberBuffer = ""
                        }
                        expressionStack.first().sub.add(currentSub)
                    } else {
                        numberBuffer += c
                    }
                }
                check(numberBuffer.isBlank()) { "NumberBuffer error with $str" }
                check(expressionStack.first() == root) { "Root error $str" }
                check(root.sub.isNotEmpty()) { "Root empty for $str" }
                check(root.sub[0].toString() == str) { "Root string check error  $str" }

                return root.sub[0]
            }
        }
    }
}