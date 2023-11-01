package puzzles

import Puzzle

class Puzzle04P1 : Puzzle() {
    override fun solve(lines: List<String>): String {
        var fullyContainsCounter = 0
        val regex = Regex("""(\d+)-(\d+),(\d+)-(\d+)""")

        for(line in lines){
            val groups = regex.find(line)!!.groups.map { it!!.value.toInt() }
            val firstElf = Area(groups[0], groups[1])
            val secondElf = Area(groups[0], groups[1])
            val fullIntersect = firstElf.isFullyContained(secondElf) || secondElf.isFullyContained(firstElf)
            if(fullIntersect){
                fullyContainsCounter++
            }
        }
        return fullyContainsCounter.toString()
    }
    data class Area(val start: Int, val end : Int)
    {
        fun isFullyContained(other : Area) : Boolean{
            return start >= other.start && end <= other.end
        }
        companion object {
            fun fromString(input: String): Area {
                val parts = input.split('-')
                return Area(parts[0].toInt(), parts[1].toInt())
            }
        }
    }
}



class Puzzle04P2 : Puzzle() {
    override fun solve(lines: List<String>): String {
        var fullyContainsCounter = 0

        for(line in lines){
            val parts = line.split(',')
            val firstElf = Area.fromString(parts[0])
            val secondElf = Area.fromString(parts[1])
            val partialOverlap = firstElf.partialOverlap(secondElf)
            println("$firstElf $secondElf $partialOverlap")
            if(partialOverlap){
                fullyContainsCounter++
            }
        }
        return fullyContainsCounter.toString()
    }
    data class Area(val start: Int, val end : Int)
    {
        fun partialOverlap(other : Area) : Boolean{
            return isFullyContainedBy(other) || other.isFullyContainedBy(this)
                    || start <= other.start && other.start <= end
                    || start <= other.end && other.end <= end

        }
        fun isFullyContainedBy(other : Area) : Boolean{
            return start >= other.start && end <= other.end
        }
        companion object {
            fun fromString(input: String): Area {
                val parts = input.split('-')
                return Area(parts[0].toInt(), parts[1].toInt())
            }
        }
    }
}