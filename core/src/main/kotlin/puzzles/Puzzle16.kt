package puzzles

import Puzzle
import java.util.*

class Puzzle16 {
    class Part1 : Puzzle() {
        override fun solve(lines: List<String>): String {
            val valveMap = parseValves(lines)

            val queue = PriorityQueue<State>()
            val aa = valveMap.getValue("AA")
            queue.add(State(aa, emptySet(), 0, 30))
            val s = shortestPath(queue, valveMap)

            return s.toString()
        }

        private fun shortestPath(
            states: PriorityQueue<State>,
            navigatableMap: Map<String, Valve>,
        ): Int {
            val visited = mutableSetOf<Int>()
            var bestFinalState: State? = null

            while (states.isNotEmpty()) {
                val first = states.first()
                if (first.minutesLeft == 0) {
                    if (bestFinalState == null || bestFinalState.pressureReleased < first.pressureReleased) {
                        bestFinalState = first
                    }
                }

                states.remove(first)
                for (c in navigatableMap.getValue(first.position.code).connections) {
                    val cV = navigatableMap.getValue(c)

                    if (first.minutesLeft >= 1) {
                        val notOpen = State(cV, first.openedValved, first.pressureReleased, first.minutesLeft - 1)
                        if (!visited.contains(notOpen.hashCode())) {
                            states.add(notOpen)
                            visited.add(notOpen.hashCode())
                        }
                    }

                    if (cV.flowRate > 0 && first.minutesLeft >= 2 && !first.openedValved.contains(cV)) {
                        val open = State(
                            cV,
                            first.openedValved + cV,
                            first.pressureReleased + cV.flowRate * (first.minutesLeft - 2),
                            first.minutesLeft - 2
                        )
                        if (!visited.contains(open.hashCode())) {
                            states.add(open)
                            visited.add(open.hashCode())
                        }
                    }
                }
            }
            return bestFinalState!!.pressureReleased
        }

        data class State(
            val position: Valve, val openedValved: Set<Valve>, val pressureReleased: Int, val minutesLeft: Int
        ) : Comparable<State> {
            override fun compareTo(other: State): Int {
                val diff = (other.pressureReleased - pressureReleased)
                if (diff != 0) {
                    return diff
                } else {
                    return (other.minutesLeft - minutesLeft)
                }
            }

        }
    }

    class Part2 : Puzzle() {
        override fun solve(lines: List<String>): String {
            val valveMap = parseValves(lines)

            val aa = valveMap.getValue("AA")
            val relevantTravelTimes = calculateTravelTimes(valveMap, aa)


            val initialState = State3(listOf(Mission(aa, 26), Mission(aa, 26)), emptySet(), 0, 26)
//            queue.add(State3(setOf(Mission(aa, 30)), emptySet(), 0, 30))
            val s = shortestPath2(initialState, relevantTravelTimes)

            return s.toString()
        }

        private fun calculateTravelTimes(valveMap: Map<String, Valve>, start: Valve): Map<Valve, Map<Valve, Int>> {
            val travelTime = mutableMapOf<Valve, MutableMap<Valve, Int>>()
            var run = true
            while (run) {
                run = false
                valveMap.values.forEach { a ->
                    valveMap.values.forEach { b ->
                        val oldT = travelTime[a]?.get(b)
                        if (oldT == null && a == b) {
                            travelTime.computeIfAbsent(a) { mutableMapOf() }.set(b, 0)
                            run = true
                        } else {
                            val minAtoB =
                                a.connections.map { valveMap.getValue(it) }
                                    .mapNotNull { travelTime.get(it)?.get(b) }
                                    .minOrNull()
                            if (minAtoB != null && (oldT == null || oldT > minAtoB + 1)) {
                                travelTime[a]?.set(b, minAtoB + 1)
                                travelTime[b]?.set(a, minAtoB + 1)
                                run = true
                            }
                        }
                    }
                }
                valveMap.values.forEach {
                }
            }
            val relevantDestinations = valveMap.values.filter { it.flowRate > 0 }.toSet()
            val relevantTravelTimes = travelTime.filter { relevantDestinations.contains(it.key) || it.key == start }
                .mapValues { it.value.filter { it.value > 0 && relevantDestinations.contains(it.key) } }
            return relevantTravelTimes
        }

        private fun shortestPath2(
            initialState: State3,
            relevantTravelTimes: Map<Valve, Map<Valve, Int>>
        ): Int {
            val states= PriorityQueue<State3>()
            val visited = mutableSetOf<Int>()
            states.add(initialState)
            visited.add(initialState.hashCode())
            var best = -1
            while (states.isNotEmpty()) {
                val current = states.first()
                states.remove(current)

                if (current.minutesLeft < 1) {
                    if (best < current.pressureReleased) {
                        best = current.pressureReleased
                        println(best)
                    }
                } else {
                    var pressureReleased = current.pressureReleased
                    val nextMissions = current.missions.map {
                        if (it.finishedAt == current.minutesLeft) {
                            pressureReleased += (it.finishedAt * it.vale.flowRate)
                            val newMissions =
                                relevantTravelTimes.getValue(it.vale)
                                    .filterNot { it.key in current.openedValves }
                                    .map { Mission(it.key, current.minutesLeft - it.value - 1) }
                                    .filter { it.finishedAt >= 0 }
                                    .toSet()
                            newMissions
                        } else {
                            listOf(it)
                        }
                    }

                    if (nextMissions.isNotEmpty() && nextMissions.get(0).isNotEmpty()) {
                        for (n in nextMissions.get(0)) {
                            val newMissionsToPair = nextMissions.getOrNull(1)
                            if (newMissionsToPair != null) {
                                for (m in newMissionsToPair) {
                                    if (n.vale != m.vale) {
                                        val newState = State3(
                                            listOf(n, m).sortedBy { it.vale.code },
                                            (current.openedValves + m.vale + n.vale),
                                            pressureReleased,
                                            Math.max(n.finishedAt, m.finishedAt)
                                        )
                                        if (!visited.contains(newState.hashCode())) {
                                            states.add(newState)
                                            visited.add(newState.hashCode())
                                        }
                                    }
                                }
                                if(newMissionsToPair.size == 1 && nextMissions.get(0).size == 1 && newMissionsToPair.first().vale == nextMissions.get(0).first().vale)
                                {
                                    val n = nextMissions.get(0).first()
                                    val newState = State3(
                                        listOf(n).sortedBy { it.vale.code },
                                        (current.openedValves + n.vale),
                                        pressureReleased,
                                        n.finishedAt
                                    )
                                    if (!visited.contains(newState.hashCode())) {
                                        states.add(newState)
                                        visited.add(newState.hashCode())
                                    }
                                }
                            } else {
                                val newState = State3(
                                    listOf(n),
                                    (current.openedValves + n.vale),
                                    pressureReleased,
                                    n.finishedAt
                                )
                                if (!visited.contains(newState.hashCode())) {
                                    states.add(newState)
                                    visited.add(newState.hashCode())
                                }
                            }
                        }
                    } else {
                        val newState = State3(
                            emptyList(),
                            current.openedValves,
                            pressureReleased,
                            current.minutesLeft - 1
                        )
                        if (!visited.contains(newState.hashCode())) {
                            states.add(newState)
                            visited.add(newState.hashCode())
                        }
                    }
                }
            }
            return best
        }

        private fun shortestPath(
            states: PriorityQueue<State2>,
            navigatableMap: Map<String, Valve>,
        ): Int {
            val visited = mutableSetOf<Int>()
            var bestFinalState: State2? = null
            val bestOfs = mutableMapOf<BestOf, Int>()

            while (states.isNotEmpty()) {
                val first = states.first()
                states.remove(first)
                val current = BestOf(first.position, first.openedValves)
                var currentBest = bestOfs[current]
                if (currentBest == null || first.pressureReleased > currentBest) {
                    bestOfs[current] = first.pressureReleased
                    currentBest = first.pressureReleased
                }
                if (first.minutesLeft == 0) {
                    if (bestFinalState == null || bestFinalState.pressureReleased < first.pressureReleased) {
                        bestFinalState = first
                        println(bestFinalState.pressureReleased)
                    }
                    continue;
                }
                if (first.pressureReleased < currentBest) {
                    continue;
                }


                if (first.minutesLeft >= 1) {
                    val positionSteps = getSteps(first.position.first(), first, navigatableMap)
                    val elephantSteps = getSteps(first.position.last(), first, navigatableMap)

                    for (p in positionSteps) {
                        for (e in elephantSteps) {
                            val openedValves = setOfNotNull(p.openedValve, e.openedValve)
                            val flow = openedValves.sumOf { it.flowRate }

                            val n = State2(
                                setOf(
                                    p.newPosition, e.newPosition
                                ),
                                openedValves = first.openedValves + openedValves,
                                pressureReleased = first.pressureReleased + flow * (first.minutesLeft - 1),
                                minutesLeft = first.minutesLeft - 1
                            )

                            if (!visited.contains(n.hashCode())) {
                                states.add(n)
                                visited.add(n.hashCode())
                            }
                        }
                    }
                }
            }
            return bestFinalState!!.pressureReleased
        }

        private fun getSteps(
            position: Valve, first: State2, navigatableMap: Map<String, Valve>
        ): List<Step> {
            val positionSteps = mutableListOf<Step>()
            if (position.flowRate > 0 && !first.openedValves.contains(position)) {
                positionSteps.add(Step(position, position))
            }
            for (c in navigatableMap.getValue(position.code).connections) {
                val cV = navigatableMap.getValue(c)
                positionSteps.add(Step(cV, null))
            }
            return positionSteps
        }


        data class BestOf(
            val position: Set<Valve>,
            val openedValves: Set<Valve>,
        )

        data class Step(
            val newPosition: Valve,
            val openedValve: Valve?,
        )

        data class Mission(val vale: Valve, val finishedAt: Int)

        data class State3(
            val missions: List<Mission>, val openedValves: Set<Valve>, val pressureReleased: Int, val minutesLeft: Int
        ) : Comparable<State3> {
            override fun compareTo(other: State3): Int {
                val diff = (other.pressureReleased - pressureReleased)
                if (diff != 0) {
                    return diff
                } else {
                    return (other.minutesLeft - minutesLeft)
                }
            }
        }

        data class State2(
            val position: Set<Valve>, val openedValves: Set<Valve>, val pressureReleased: Int, val minutesLeft: Int
        ) : Comparable<State2> {

            var atBest = -1

            init {
                atBest = pressureReleased
            }

            override fun compareTo(other: State2): Int {
                val diff = (other.atBest - atBest)
                if (diff != 0) {
                    return diff
                } else {
                    return (other.minutesLeft - minutesLeft)
                }
            }
        }
    }

    class Valve(val code: String, val flowRate: Int, val connections: List<String>)


    companion object {
        private fun parseValves(lines: List<String>): Map<String, Valve> {
            val valves = mutableListOf<Valve>()

            for (l in lines) {
                val code = l.substringAfter("Valve ").split(" ")[0]
                val flowRate = l.substringAfter("flow rate=").split(";")[0].toInt()
                val connections = l.substringAfter(" to valve").drop(1).trim().split(", ")
                valves.add(Valve(code, flowRate, connections))
            }
            val valveMap = valves.associateBy { it.code }

            return valveMap
        }

    }

}

