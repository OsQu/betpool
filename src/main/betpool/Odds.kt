package betpool

import kotlin.math.floor

data class Competitor(val name: String, val odds: Int)

data class Odds(private var odds: Map<String, Competitor>) {
    init {
        if (odds.values.any { competitor -> competitor.odds < 100 }) {
            throw IllegalArgumentException("Odds cannot be less than 1")
        }
        odds = scaleOdds(odds)
    }
    companion object {
        private fun scaleOdds(initialOdds: Map<String, Competitor>): Map<String, Competitor> {
            var summedProbabilities: Float = 0f
            initialOdds.values.forEach {
                summedProbabilities += 1 / (it.odds/100f)
            }
            return initialOdds.mapValues { Competitor(it.value.name, floor(it.value.odds * summedProbabilities).toInt()) }
        }
    }

    fun containsId(oddsId: String): Boolean {
        return odds.containsKey(oddsId)
    }

    fun getOddsWithNames(): Map<String, Competitor> {
        return odds.toMap()
    }

    fun getOdds(): Map<String, Int> {
        return odds.mapValues { it.value.odds }
    }
}
