package puzzles

import Puzzle

class Puzzle06P1 : Puzzle() {
    override fun solve(lines : List<String>): String {
        val line = lines[0]
        for(i in 0..line.length-4){
            val substring = line.substring(i,i+4)
            val size = substring.toSet().size
            if(size == 4) {
                return (i+4).toString()
            }
        }
        return "null"
    }
}

class Puzzle06P2 : Puzzle() {
    override fun solve(lines : List<String>): String {
        val line = lines[0]
        for(i in 0..line.length-14){
            val substring = line.substring(i,i+14)
            val size = substring.toSet().size
            if(size == 14) {
                return (i+14).toString()
            }
        }
        return "null"
    }

}