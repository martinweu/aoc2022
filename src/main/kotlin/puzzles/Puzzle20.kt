package puzzles

import Puzzle

class Puzzle20 {
    class Part1 : Puzzle() {
        override fun solve(lines: List<String>): String {
            val input = lines.mapIndexed { index, element -> Element(element.toLong(), index, null, null) }

            linkElementsToNextAndPrevious(input)

            for (e in input) {
                val moves = e.value
                e.move(moves)
            }

            return calculateSolution(input)
        }
    }

    class Part2 : Puzzle() {
        override fun solve(lines: List<String>): String {
            val decryptionKey = 811589153L
            val input = lines.mapIndexed { index, element -> Element(element.toLong() * decryptionKey, index, null, null) }

            linkElementsToNextAndPrevious(input)

            for (i in 0 until 10) {
                for (e in input) {
                    val moves = e.value % (input.size - 1)
                    e.move(moves)
                }
            }
            return calculateSolution(input)
        }
    }

    companion object {

        private fun linkElementsToNextAndPrevious(input: List<Element>) {
            var previous = input.last()
            input.forEachIndexed { index, element ->
                element.previous = previous
                element.next = input[(index + 1) % input.size]
                previous = element
            }
        }

        private fun calculateSolution(input: List<Element>): String {
            var current = input.first { it.value == 0L }
            val numbers = mutableListOf<Long>()

            for (i in 1..3000) {
                current = current.next!!
                if (i % 1000 == 0) {
                    numbers.add(current.value)
                    println(current.value)
                }

            }
            return numbers.sum().toString()
        }

    }

    class Element(
        val value: Long,
        val initialPosition: Int,
        var previous: Element?,
        var next: Element?
    ) {
        override fun toString(): String {
            return "Element(value=$value, initialPosition=$initialPosition, previous=${previous?.value}, next=${next?.value})"
        }

        fun move(moves: Long) {
            var movesRemaining = moves
            while (movesRemaining > 0) {
                this.previous!!.next = this.next
                this.next!!.previous = this.previous
                val myOldNext = this.next!!
                this.previous = myOldNext
                this.next = this.next!!.next
                myOldNext.next = this
                this.next!!.previous = this
                movesRemaining--
            }
            while (movesRemaining < 0) {
                this.next!!.previous = this.previous
                this.previous!!.next = this.next
                val myOldPrevious = this.previous!!
                this.next = myOldPrevious
                this.previous = this.previous!!.previous
                myOldPrevious.previous = this
                this.previous!!.next = this
                movesRemaining++
            }
        }
    }
}