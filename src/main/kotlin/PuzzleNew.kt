import java.io.File
import java.util.*

abstract class PuzzleNew {
    private val puzzle: String
    private val part: String

    init {
        val solverClassName = this.javaClass.name.split(".").last()
        if (solverClassName.endsWith("P1") || solverClassName.endsWith("P2")) {
            this.puzzle = solverClassName.substring("Puzzle".length, solverClassName.length - 2)
            this.part = solverClassName.takeLast(2)
        } else {
            val splittedSolverName = solverClassName.split("$")
            this.puzzle = splittedSolverName[0].substringAfter("Puzzle")
            this.part = "P" + splittedSolverName[1].last()
        }
    }

    val name get() = "Puzzle: $puzzle Part: $part"

    val basePath: String get() = "puzzles/$puzzle/$part"

    fun solveFile(inputFile: File): String {
        val scanner = Scanner(inputFile)
        return solve(scanner)
    }

    protected abstract fun solve(scanner: Scanner): String
}