package puzzles

import Puzzle

class Puzzle20 {
    class Part1 : Puzzle() {
        override fun solve(lines: List<String>): String {
            val input = lines.mapIndexed { index, element -> Element(element.toLong(), index, false, null, null) }

            var previous = input.last()
            input.forEachIndexed { index, element ->
                element.previous = previous
                element.next = input[(index + 1) % input.size]
                previous = element
            }

            for (e in input) {
                var moves = e.value
                while (moves > 0) {
                    e.previous!!.next = e.next
                    e.next!!.previous = e.previous
                    val myOldNext = e.next!!
                    e.previous = myOldNext
                    e.next = e.next!!.next
                    myOldNext.next = e
                    e.next!!.previous = e
                    moves--
                }
                while (moves < 0) {
                    e.next!!.previous = e.previous
                    e.previous!!.next = e.next
                    val myOldPrevious = e.previous!!
                    e.next = myOldPrevious
                    e.previous = e.previous!!.previous
                    myOldPrevious.previous = e
                    e.previous!!.next = e
                    moves++
                }
            }

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

    class Part2 : Puzzle() {
        override fun solve(lines: List<String>): String {
            val decryptionKey = 811589153L
            val input = lines.mapIndexed { index, element -> Element(element.toLong() * decryptionKey, index, false, null, null) }

            var previous = input.last()
            input.forEachIndexed { index, element ->
                element.previous = previous
                element.next = input[(index + 1) % input.size]
                previous = element
            }

            for(i in 0 until 10) {
                for (e in input) {
                    var moves = e.value % (input.size-1)
                    while (moves > 0) {
                        e.previous!!.next = e.next
                        e.next!!.previous = e.previous
                        val myOldNext = e.next!!
                        e.previous = myOldNext
                        e.next = e.next!!.next
                        myOldNext.next = e
                        e.next!!.previous = e
                        moves--
                    }
                    while (moves < 0) {
                        e.next!!.previous = e.previous
                        e.previous!!.next = e.next
                        val myOldPrevious = e.previous!!
                        e.next = myOldPrevious
                        e.previous = e.previous!!.previous
                        myOldPrevious.previous = e
                        e.previous!!.next = e
                        moves++
                    }
                }
            }

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
        var moved: Boolean = false,
        var previous: Element?,
        var next: Element?
    ){
        override fun toString(): String {
            return "Element(value=$value, initialPosition=$initialPosition, moved=$moved, previous=${previous?.value}, next=${next?.value})"
        }
    }
}