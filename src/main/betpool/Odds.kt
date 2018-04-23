package betpool

import kotlin.math.floor

data class Competitor(val name: String, val odds: Int)
// Todo: odds cannot be 0

data class Odds(private var odds: Map<String, Competitor>) {
    init {
        odds = Odds.scaleOdds(odds)
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
