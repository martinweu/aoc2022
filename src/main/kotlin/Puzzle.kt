import java.io.File

abstract class Puzzle {
    private val puzzle: String
    private val part: String

    init {
        val solverClassName = this.javaClass.name.split(".").last()
        this.puzzle = solverClassName.substring("Puzzle".length, solverClassName.length-2)
        this.part = solverClassName.takeLast(2)
    }

    val name get() = "Puzzle: $puzzle Part: $part"

    val basePath: String get() = "puzzles/$puzzle/$part"

    fun solveFile(inputFile: File): String {
        val input = inputFile.readLines()
        return solve(input)
    }

    protected abstract fun solve(lines: List<String>): String

}