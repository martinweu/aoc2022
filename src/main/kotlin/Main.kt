import org.apache.commons.io.FileUtils
import puzzles.*
import java.io.File

val PUZZLE = Puzzle16.Part1()

fun main() {
   runFile("example")
   runFile("my")
}

fun runFile(filename: String) {
    val file = PUZZLE.basePath + "/" + filename

    val inputFile = File(file + ".in")
    val checkFile = File(file + ".check")

    val existingCheckFile = if (checkFile.exists()) {
        checkFile
    } else {
        null
    }
    computeAndCheck(inputFile, existingCheckFile)
}

fun computeAndCheck(inputFile: File, checkFile: File?) {
    if (FileUtils.sizeOf(inputFile) == 0L) {
        throw RuntimeException("Empty Input for ${inputFile.name} using ${PUZZLE.name}")
    }
    val foundSolution = PUZZLE.solveFile(inputFile)
    if (foundSolution.isBlank()) {
        throw RuntimeException("Empty Solution for ${inputFile.name} using ${PUZZLE.name}")
    }

    if (checkFile == null) {
        System.err.println("Unchecked Solution for ${inputFile.name} using ${PUZZLE.name} is $foundSolution")
    } else {
        val expectedSolution = checkFile.readLines().joinToString("\n")
        if (expectedSolution != foundSolution) {
            System.err.println("Wrong Solution for ${inputFile.name} using ${PUZZLE.name} with $foundSolution, expected is $expectedSolution")
        }
        else{
            println("Correct Solution for ${inputFile.name} using ${PUZZLE.name} is $foundSolution")
        }
    }
}