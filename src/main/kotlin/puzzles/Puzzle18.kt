package puzzles

import Puzzle

class Puzzle18 {
    class Part1 : Puzzle() {
        override fun solve(lines: List<String>): String {

            val lavaCubes = lines.map {
                Cube(Vector3D.fromString(it))
            }

            val areas = mutableMapOf<Set<Vector3D>, Int>()
            for (cube in lavaCubes) {
                cube.getSurface().forEach {
                    areas[it] = (areas[it] ?: 0) + 1
                }
            }
            return areas.filter { it.value == 1 }.size.toString()
        }
    }

    data class Cube(val position: Vector3D) {
        fun getSurface(): Set<Set<Vector3D>> {
            val bottom00 = position
            val bottom10 = position + Vector3D.X
            val bottom01 = position + Vector3D.Y
            val bottom11 = position + Vector3D.X + Vector3D.Y

            val top00 = bottom00 + Vector3D.Z
            val top10 = bottom10 + Vector3D.Z
            val top01 = bottom01 + Vector3D.Z
            val top11 = bottom11 + Vector3D.Z

            return setOf(
                setOf(
                    bottom00, bottom01, bottom11, bottom10
                ),
                setOf(
                    top00, top01, top11, top10
                ),
                setOf(
                    bottom00, top00, bottom01, top01
                ),
                setOf(
                    bottom01, top01, bottom11, top11
                ),
                setOf(
                    bottom11, top11, bottom10, top10
                ),
                setOf(
                    bottom10, top10, bottom00, top00
                )
            )
        }
    }

    data class Vector3D(val x: Long, val y: Long, val z: Long) {
        operator fun plus(b: Vector3D): Vector3D {
            return Vector3D(x + b.x, y + b.y, z + b.z)
        }

        operator fun times(b: Int): Vector3D {
            return Vector3D(x * b, y * b, z * b)
        }

        operator fun minus(b: Vector3D): Vector3D {
            return Vector3D(x - b.x, y - b.y, z - b.z)
        }

        companion object {

            val X = Vector3D(1, 0, 0)
            val Y = Vector3D(0, 1, 0)
            val Z = Vector3D(0, 0, 1)
            fun fromString(str: String): Vector3D {
                val parts = str.split(",").map { it.trim().toLong() }
                return Vector3D(parts[0], parts[1], parts[2])
            }
        }
    }

    class Part2 : Puzzle() {
        override fun solve(lines: List<String>): String {

            val lavaCubes = lines.map {
                Cube(Vector3D.fromString(it))
            }

            val areas = mutableMapOf<Set<Vector3D>, Int>()
            for (cube in lavaCubes) {
                cube.getSurface().forEach {
                    areas[it] = (areas[it] ?: 0) + 1
                }
            }

            val outsideAreas = getOutsideCubes(lavaCubes)
                .flatMap { it.getSurface() }.toSet()

            return (areas.filter { it.value == 1 }.keys.intersect(outsideAreas)).size.toString()

        }

        private fun getOutsideCubes(lavaCubes: List<Cube>): Set<Cube> {
            var min = Vector3D(Long.MAX_VALUE, Long.MAX_VALUE, Long.MAX_VALUE)
            var max = Vector3D(Long.MIN_VALUE, Long.MIN_VALUE, Long.MIN_VALUE)
            lavaCubes.forEach {
                val pos = it.position
                min = Vector3D(Math.min(min.x, pos.x), Math.min(min.y, pos.y), Math.min(min.z, pos.z))
                max = Vector3D(Math.max(max.x, pos.x), Math.max(max.y, pos.y), Math.max(max.z, pos.z))
            }

            val connectedToOutside = mutableMapOf<Vector3D, Outside>()

            lavaCubes.forEach { connectedToOutside[it.position] = Outside.NO }

            var run = true
            while (run) {
                run = false
                for (x in min.x - 1..max.x + 1) {
                    for (y in min.y - 1..max.y + 1) {
                        for (z in min.z - 1..max.z + 1) {
                            val v = Vector3D(x, y, z)
                            if (x < min.x || x > max.x || y < min.y || y > max.y || z < min.z || z > max.z) {
                                connectedToOutside[v] = Outside.YES
                            }
                            if (connectedToOutside.getOrDefault(v, Outside.UNSURE) == Outside.UNSURE) {
                                val outsides = setOf(
                                    v + Vector3D.X,
                                    v - Vector3D.X,
                                    v + Vector3D.Y,
                                    v - Vector3D.Y,
                                    v + Vector3D.Z,
                                    v - Vector3D.Z,
                                ).map { connectedToOutside.computeIfAbsent(it) { Outside.UNSURE } }
                                val outside = Outside.aggregate(outsides)
                                connectedToOutside[v] = outside
                                if (outside == Outside.YES) {
                                    run = true
                                }
                            }
                        }
                    }
                }
            }

            return connectedToOutside.filter { it.value == Outside.YES }.map { Cube(it.key) }.toSet()
        }

        enum class Outside {
            NO,
            UNSURE,
            YES;

            companion object {
                fun aggregate(sides: Collection<Outside>): Outside {
                    return sides.max()
                }
            }
        }

    }
}