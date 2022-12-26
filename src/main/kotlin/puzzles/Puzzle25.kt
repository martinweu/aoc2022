package puzzles

import Puzzle

class Puzzle25 {
    class Part1 : Puzzle() {
        override fun solve(lines: List<String>): String {
            val res = lines.reduce { acc, s -> add(acc, s) }
            return res
        }

        fun add(a: String, b: String): String {
            val upTo = Math.max(a.length, b.length)
            var carry = '0'
            var index = 0
            var result = ""
            while (carry != '0' || index < upTo) {
                val ca = a.getOrNull(a.length - index - 1) ?: '0'
                val cb = b.getOrNull(b.length - index - 1) ?: '0'
                val (v, generatedCarry) = add(ca, cb)
                val (u, carrycarry) = add(v, carry)
                val (carryNew, ccf) = add(generatedCarry, carrycarry)
                check(ccf == '0')
                carry = carryNew
                result = u + result
                index++
            }

            return result
        }
        fun add(a: Char, b: Char): Pair<Char, Char> {
            val charSet = setOf(a, b)
            if (charSet.contains('0')) {
                return Pair(charSet.firstOrNull { it != '0' } ?: '0', '0');
            }

            if (charSet == setOf('2')) {
                return Pair('-', '1');
            }
            if (charSet == setOf('2', '1')) {
                return Pair('=', '1');
            }
            if (charSet == setOf('2', '-')) {
                return Pair('1', '0');
            }
            if (charSet == setOf('2', '=')) {
                return Pair('0', '0');
            }
            if (charSet == setOf('1')) {
                return Pair('2', '0');
            }
            if (charSet == setOf('1', '-')) {
                return Pair('0', '0');
            }
            if (charSet == setOf('1', '=')) {
                return Pair('-', '0');
            }
            if (charSet == setOf('-')) {
                return Pair('=', '0');
            }
            if (charSet == setOf('-', '=')) {
                return Pair('2', '-');
            }
            if (charSet == setOf('=')) {
                return Pair('1', '-');
            }
            throw Exception("Unsupported: a: $a b: $b")
        }
    }

    class Part2 : Puzzle() {
        override fun solve(lines: List<String>): String {
            TODO("$name is not yet implemented")
        }

    }

    companion object {

    }
}