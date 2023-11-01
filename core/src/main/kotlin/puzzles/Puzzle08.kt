package puzzles

import Puzzle

class Puzzle08P1 : Puzzle() {
    override fun solve(lines: List<String>): String {

        val visible = mutableListOf<MutableList<String>>()

        for (i in 0 until lines.size) {
            val currentLine = mutableListOf<String>()
            for (j in 0 until lines[0].length) {
                currentLine.add("")
            }
            visible.add(currentLine)
        }

        var sweep = "L"

        for (i in 0 until visible.size) {
            var maxLine = lines[i][0]
            visible[i][0] += sweep
            for (j in 1 until visible[i].size) {
                val c = lines[i][j]
                if (maxLine < c) {
                    visible[i][j] += sweep
                    maxLine = c
                } else if (maxLine == '9') {
                    break;
                }
            }
        }

        sweep = "R"

        for (i in 0 until visible.size) {
            var maxLine = lines[i][visible[0].size - 1]
            visible[i][visible[0].size - 1] += sweep
            for (j in visible[i].size - 2 downTo 0) {
                val c = lines[i][j]
                if (maxLine < c) {
                    visible[i][j] += sweep
                    maxLine = c
                } else if (maxLine == '9') {
                    break;
                }
            }
        }

        sweep = "U"

        for (i in 0 until visible[0].size) {
            var maxLine = lines[0][i]
            visible[0][i] += sweep
            for (j in 1 until visible.size) {
                val c = lines[j][i]
                if (maxLine < c) {
                    visible[j][i] += sweep
                    maxLine = c
                } else if (maxLine == '9') {
                    break;
                }
            }
        }

        sweep = "D"

        for (i in visible[0].size - 1 downTo 0) {
            var maxLine = lines[visible[0].size - 1][i]
            visible[visible[0].size - 1][i] += sweep
            for (j in visible[i].size - 2 downTo 0) {
                val c = lines[j][i]
                if (maxLine < c) {
                    visible[j][i] += sweep
                    maxLine = c
                } else if (maxLine == '9') {
                    break;
                }
            }
        }

        val invisible = visible.sumOf { it.count { it.isBlank() } }
        val total = visible.size * visible[0].size
        return (total - invisible).toString()
    }
}

class Puzzle08P2 : Puzzle() {
    override fun solve(lines: List<String>): String {

        var bestTreeScore = 0


        for (i in 0 until lines.size) {
            val currentLine = mutableListOf<String>()
            for (j in 0 until lines[0].length) {

                val currentTree = lines[i][j]

                // Down
                var treeCount = 0
                var treeScore = 1
                for (vi in i+1 until lines.size) {
                    if (lines[vi][j] < currentTree) {
                        treeCount++
                    } else {
                        treeCount++
                        break;
                    }
                }
                treeScore *= treeCount

                // Up
                treeCount = 0
                for (vi in i-1 downTo 0) {
                    if (lines[vi][j] < currentTree) {
                        treeCount++
                    } else {
                        treeCount++
                        break;
                    }
                }
                treeScore *= treeCount

                //Left
                treeCount = 0
                for (vj in j+1 until lines[0].length) {
                    if (lines[i][vj] < currentTree) {
                        treeCount++
                    } else {
                        treeCount++
                        break;
                    }
                }

                treeScore *= treeCount

                //Right
                treeCount = 0
                for (vj in j-1 downTo 0) {
                    if (lines[i][vj] < currentTree) {
                        treeCount++
                    } else {
                        treeCount++
                        break;
                    }
                }
                treeScore *= treeCount

                if(bestTreeScore < treeScore) {
                    bestTreeScore = treeScore
                }
            }
        }
        return bestTreeScore.toString()
    }

}