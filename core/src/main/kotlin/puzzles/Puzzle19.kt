package puzzles

import Puzzle
import java.util.*

class Puzzle19 {
    class Part1 : Puzzle() {
        override fun solve(lines: List<String>): String {
            val blueprints = parseBlueprints(lines)
            val time = 24

            val results = blueprints.associateWith {
                getBestOutput(it, State(time, Ressources(ore = 1), Ressources()))
            }

            val qualityLevel = results.map { it.key.id * it.value }.sum()
            return qualityLevel.toString()
        }


    }

    class Part2 : Puzzle() {
        override fun solve(lines: List<String>): String {
            val blueprints = parseBlueprints(lines)
            val time = 32

            val results = blueprints.take(3).associateWith {
                getBestOutput(it, State(time, Ressources(ore = 1), Ressources()))
            }

            val score = results.values.reduce { acc, c -> acc * c }
            return score.toString()
        }
    }

    companion object {

        fun getBestOutput(blueprint: Blueprint, initialState: State): Int {
            val priorityQueue = PriorityQueue<State>()
            priorityQueue.add(initialState)
            val visited = mutableSetOf<State>()
            var bestSolution = -1

            val maxCosts =
                blueprint.robots.map { it.costs }.reduce { acc, robotCosts -> acc.max(robotCosts) }

            while (priorityQueue.isNotEmpty()) {
                val current = priorityQueue.first()
                priorityQueue.remove(current)
                if (current.timeLeft == 0) {
                    if (bestSolution < current.stock.geode) {
                        bestSolution = current.stock.geode
                        println("Blueprint: ${blueprint.id} with score: $bestSolution with stock: ${current.stock}")
                        return bestSolution
                    }
                } else {
                    val newTime = current.timeLeft - 1
                    val newStock = current.stock + current.production

//                    if ((maxCosts * newTime).anyGreaterEqualThan(current.stock + (current.production * newTime))) {
                    for (robot in blueprint.robots) {
                        if (current.stock.allGreaterEqualThan(robot.costs) && !robot.costs.allGreaterEqualThan(robot.output * newTime)) {
                            val nextState = State(
                                timeLeft = newTime,
                                stock = newStock - robot.costs,
                                production = current.production + robot.output,
                            )
                            if(!visited.contains(nextState)) {
                                priorityQueue.add(nextState)
                                visited.add(nextState)
                            }
                        }
                    }
//                    }
                    val nextState = State(
                        timeLeft = newTime,
                        stock = newStock,
                        production = current.production,
                    )
                    if(!visited.contains(nextState)) {
                        priorityQueue.add(nextState)
                        visited.add(nextState)
                    }
                }
            }
            return bestSolution
        }
        private fun parseBlueprints(lines: List<String>): MutableList<Blueprint> {
            val regex = Regex(
                """Blueprint (\d+): Each ore robot costs (\d+) ore. Each clay robot costs (\d+) ore. Each obsidian robot costs (\d+) ore and (\d+) clay. Each geode robot costs (\d+) ore and (\d+) obsidian.
                    """.trimIndent()
            )

            val blueprints = mutableListOf<Blueprint>()

            for (line in lines) {
                val parts = regex.find(line)!!.groups.drop(1).map { it!!.value.toInt() }
                val currentBlueprint = Blueprint(
                    parts[0], setOf(
                        Robot(Ressources(ore = parts[1]), Ressources(ore = 1)),
                        Robot(Ressources(ore = parts[2]), Ressources(clay = 1)),
                        Robot(Ressources(ore = parts[3], clay = parts[4]), Ressources(obsidian = 1)),
                        Robot(Ressources(ore = parts[5], obsidian = parts[6]), Ressources(geode = 1)),
                    )
                )
                blueprints.add(currentBlueprint)
            }
            return blueprints
        }
    }

    data class State(
        val timeLeft: Int,
        val production: Ressources,
        val stock: Ressources,
    ) : Comparable<State> {

        fun getPotentialGeodes(): Int {
            return stock.geode + (production.geode * timeLeft) + Math.max(((timeLeft) * (timeLeft-1)/2),0)
        }

        override fun compareTo(other: State): Int {

            return other.getPotentialGeodes() - getPotentialGeodes()
//            var res = other.stock.geode - stock.geode
//            if(res != 0)
//            {
//                return res
//            }
//            res = other.production.geode - production.geode
//            if(res != 0)
//            {
//                return res
//            }
//            res = other.timeLeft - timeLeft
//            return res
        }
    }

    data class Ressources2(val bytes: Long)
    {
        constructor(ore : Int = 0, clay: Int = 0, obsidian: Int = 0, geode: Int = 0):this(0L + ore + (clay shl 16)  + (obsidian shl 32) + (geode shl 48))

        val ore get() = ((bytes) and (512-1)).toInt()
        val clay get() = ((bytes shr 16) and (512-1)).toInt()
        val obsidian get() = ((bytes shr 32) and (512-1)).toInt()
        val geode get() = ((bytes shr 48) and (512-1)).toInt()
//        init {
//            check(ore in 0..511)
//            check(clay in 0..511)
//            check(obsidian in 0..511)
//            check(geode in 0..511)
//            bytes = 0L + ore + (clay shl 16)  + (obsidian shl 32) + (geode shl 48)
//        }
        operator fun plus(other: Ressources2): Ressources2 {
//            return Ressources2(bytes  + other.bytes)
            return Ressources2(ore + other.ore, clay + other.clay, obsidian + other.obsidian, geode + other.geode)
        }

        operator fun times(other: Int): Ressources2 {
//            return Ressources2(bytes * other)
            return Ressources2(ore * other, clay * other, obsidian * other, geode * other)
        }

        operator fun minus(other: Ressources2): Ressources2 {
//            return Ressources2(bytes - other.bytes)
            return Ressources2(ore - other.ore, clay - other.clay, obsidian - other.obsidian, geode - other.geode)
        }

        fun max(other: Ressources2): Ressources2 {
            return Ressources2(
                Math.max(ore, other.ore),
                Math.max(clay, other.clay),
                Math.max(obsidian, other.obsidian),
                Math.max(geode, other.geode)
            )
        }

        fun allGreaterEqualThan(other: Ressources2): Boolean {
            return setOf(this.ore >= other.ore, this.clay >= other.clay, this.obsidian >= other.obsidian, this.geode >= other.geode).all{it}
        }

        fun anyGreaterEqualThan(other: Ressources2): Boolean {
            return setOf(this.ore >= other.ore, this.clay >= other.clay, this.obsidian >= other.obsidian, this.geode >= other.geode).any{it}
        }
    }


    data class Ressources(
        val ore: Int = 0,
        val clay: Int = 0,
        val obsidian: Int = 0,
        val geode: Int = 0,
    ) {
        operator fun plus(other: Ressources): Ressources {
            return Ressources(ore + other.ore, clay + other.clay, obsidian + other.obsidian, geode + other.geode)
        }

        operator fun times(other: Int): Ressources {
            return Ressources(ore * other, clay * other, obsidian * other, geode * other)
        }

        operator fun minus(other: Ressources): Ressources {
            return Ressources(ore - other.ore, clay - other.clay, obsidian - other.obsidian, geode - other.geode)
        }

        fun max(other: Ressources): Ressources {
            return Ressources(
                Math.max(ore, other.ore),
                Math.max(clay, other.clay),
                Math.max(obsidian, other.obsidian),
                Math.max(geode, other.geode)
            )
        }

        fun allGreaterEqualThan(other: Ressources): Boolean {
            val result = (this - other)
            return setOf(result.ore, result.clay, result.obsidian, result.geode).min() >= 0
        }

        fun anyGreaterEqualThan(other: Ressources): Boolean {
            val result = (this - other)
            return setOf(result.ore, result.clay, result.obsidian, result.geode).max() >= 0
        }
    }

    data class Blueprint(
        val id: Int,
        val robots: Set<Robot>
    )

    data class Robot(
        val costs: Ressources,
        val output: Ressources,
    )
}