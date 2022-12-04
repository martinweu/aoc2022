import org.apache.commons.io.FileUtils
import puzzles.*
import java.io.File

val PUZZLE = Puzzle04P2()

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
        if (checkFile.readLines()[0] != foundSolution) {
            throw RuntimeException("Wrong Solution for ${inputFile.name} using ${PUZZLE.name} with $foundSolution")
        }
        else{
            println("Correct Solution for ${inputFile.name} using ${PUZZLE.name} is $foundSolution")
        }
    }
}