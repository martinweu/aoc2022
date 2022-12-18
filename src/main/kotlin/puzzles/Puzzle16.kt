package puzzles

import Puzzle
import java.util.*

class Puzzle16 {
    class Part1 : Puzzle() {
        override fun solve(lines: List<String>): String {
            val valveMap = parseValves(lines)
//            valveMap
//                .mapValues { (k,v) -> Valve(v.code, v.flowRate, v.connections.map{ valveMap.getValue(it) }) }

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

//        private fun shortestPath1(
//            states: PriorityQueue<State>,
//            navigatableMap: Map<String, Valve>,
//        ): Int {
//            val visited = mutableSetOf<Int>()
//            var bestFinalState: State? = null
//
//            while (states.isNotEmpty()) {
//                val first = states.first()
//                states.remove(first)
//                if (first.minutesLeft == 0) {
//                    if (bestFinalState == null || bestFinalState.pressureReleased < first.pressureReleased) {
//                        bestFinalState = first
//                    }
//
//                    continue;
//                }
//                if ((30 - first.minutesLeft) > (first.openedValved.size + 1) * navigatableMap.size) {
//                    continue
//                }
//                if (first.minutesLeft >= 1) {
//                    if (first.position.flowRate > 0 && !first.openedValved.contains(first.position)) {
//                        val open = State(
//                            first.position,
//                            first.openedValved + first.position,
//                            first.pressureReleased + first.position.flowRate * (first.minutesLeft - 1),
//                            first.minutesLeft - 1
//                        )
//                        if (!visited.contains(open.hashCode())) {
//                            states.add(open)
//                            visited.add(open.hashCode())
//                        }
//                    }
//                    for (c in navigatableMap.getValue(first.position.code).connections) {
//                        val cV = navigatableMap.getValue(c)
//                        val notOpen = State(cV, first.openedValved, first.pressureReleased, first.minutesLeft - 1)
//                        if (!visited.contains(notOpen.hashCode())) {
//                            states.add(notOpen)
//                            visited.add(notOpen.hashCode())
//                        }
//                    }
//                }
//            }
//            return bestFinalState!!.pressureReleased
//        }

        data class State(
            val position: Valve,
            val openedValved: Set<Valve>,
            val pressureReleased: Int,
            val minutesLeft: Int
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
//            valveMap
//                .mapValues { (k,v) -> Valve(v.code, v.flowRate, v.connections.map{ valveMap.getValue(it) }) }

            val queue = PriorityQueue<State2>()
            val aa = valveMap.getValue("AA")
            queue.add(State2(setOf(aa), emptySet(), 0, 26))
//            queue.add(State2(aa, aa, emptySet(), valveMap.values.toSet(), 0, 26))
            val s = shortestPath(queue, valveMap)

            return s.toString()
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


//                if ((30 - first.minutesLeft) > (first.openedValved.size + 1) * navigatableMap.size) {
//                    continue
//                }
                if (first.minutesLeft >= 1) {
                    val positionSteps = getSteps(first.position.first(), first, navigatableMap)
                    val elephantSteps = getSteps(first.position.last(), first, navigatableMap)

                    for (p in positionSteps) {
                        for (e in elephantSteps) {
                            val openedValves = setOfNotNull(p.openedValve, e.openedValve)
                            val flow = openedValves.sumOf { it.flowRate }

                            val n = State2(
                                setOf(
                                    p.newPosition,
                                    e.newPosition
                                ),
                                        openedValves = first . openedValves +openedValves,
//                                closedValved = first.closedValved- openedValves,
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
            position: Valve,
            first: State2,
            navigatableMap: Map<String, Valve>
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
//            val elephant: Valve,
            val openedValves: Set<Valve>,
        )

        data class Step(
            val newPosition: Valve,
            val openedValve: Valve?,
        )

        data class State2(
            val position: Set<Valve>,
            val openedValves: Set<Valve>,
//            val closedValved : Set<Valve>,
            val pressureReleased: Int,
            val minutesLeft: Int
        ) : Comparable<State2> {

            var atBest = -1

            init {
                atBest = pressureReleased
//                + closedValved.map { it.flowRate }.sortedDescending().take(minutesLeft)
//                    .mapIndexed { index, i -> i * (minutesLeft - index) }.sum()
//                if (position in closedValved)
//                {
//                    position.flowRate * (minutesLeft-1)
//                }
//                else
//                {
//                    0
//                }
            }

            override fun compareTo(other: State2): Int {
                val diff = (other.atBest - atBest)
//                return diff
                if (diff != 0) {
                    return diff
                } else {
                    return (other.minutesLeft - minutesLeft)
                }
            }
        }
    }

    class Valve(val code: String, val flowRate: Int, val connections: List<String>)
//    class Valve(val code: String, val flowRate: Int, val connections: List<Valve>)


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

