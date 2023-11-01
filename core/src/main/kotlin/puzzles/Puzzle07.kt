package puzzles

import Puzzle

class Puzzle07P1 : Puzzle() {
    override fun solve(lines: List<String>): String {
        val pathDirMap = parseFileStructure(lines)
        val dirs = pathDirMap.values.filter { it.getDirSize() < 100_000}
        return dirs.sumOf { it.getDirSize() }.toString()
    }
}



data class Directory(
    val directoryPath: String,
    val files: MutableSet<File> = mutableSetOf(),
    val subDirectories: MutableSet<Directory> = mutableSetOf()
) {
    fun getDirSize(): Long {
        return files.map { it.fileSize }.sum() + subDirectories.map { it.getDirSize() }.sum()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Directory

        if (directoryPath != other.directoryPath) return false

        return true
    }

    override fun hashCode(): Int {
        return directoryPath.hashCode()
    }

}

data class File(val fileName: String, val fileSize: Long)
{
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as File

        if (fileName != other.fileName) return false

        return true
    }

    override fun hashCode(): Int {
        return fileName.hashCode()
    }
}

class Puzzle07P2 : Puzzle() {
    override fun solve(lines: List<String>): String {
        val pathDirMap = parseFileStructure(lines)

        val allDataSize = pathDirMap.getValue("").getDirSize()
        val totalSpace = 70000000
        val updateSize = 30000000
        val freeSpaceNow = (totalSpace - allDataSize)
        val deleteMin = updateSize - freeSpaceNow

        return pathDirMap.values.filter { it.getDirSize() > deleteMin }.sortedBy { it.getDirSize() }[0].getDirSize().toString()
    }

}

private fun parseFileStructure(lines: List<String>): MutableMap<String, Directory> {
    val currentPath = ArrayDeque<String>()
    val pathDirMap = mutableMapOf<String, Directory>()
    val root = Directory("")
    pathDirMap[root.directoryPath] = root
    var currentDirectory: Directory = root

    for (line in lines) {
        if (line.startsWith("$")) {
            val parts = line.split(" ")
            val command = parts[1]
            val argument = if (parts.size > 2) parts[2] else null

            if (command == "cd") {
                if (argument == "/") {
                    currentPath.clear()
                } else if (argument == "..") {
                    currentPath.removeLast()
                } else {
                    currentPath.addLast(argument!!)
                }
                currentDirectory = pathDirMap.getValue(currentPath.joinToString("/"))
            } else if (command == "ls") {
                //TODO: nothing
            } else {
                error("Nothing behind the $")
            }
        } else {
            if (line.startsWith("dir")) {
                val parts = line.split(" ")
                val directoryName = parts[1]
                val path = (currentPath.toList() + directoryName).joinToString("/")
                val foundDir = Directory(path)
                pathDirMap[path] = foundDir
                if (!currentDirectory.subDirectories.contains(foundDir)) {
                    currentDirectory.subDirectories.add(foundDir)
                }

            } else {
                val parts = line.split(" ")
                val fileSize = parts[0].toLong()
                val fileName = parts[1]
                val file = File(fileName, fileSize)
                if (!currentDirectory.files.contains(file)) {
                    currentDirectory.files.add(file)
                }
            }
        }
    }
    return pathDirMap
}