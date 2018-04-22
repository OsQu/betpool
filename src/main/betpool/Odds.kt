package betpool

import kotlin.math.floor

data class Competitor(val name: String, val odds: Int)
// Todo: odds cannot be 0

class Odds(initialOdds: Map<String, Competitor>) {
    private val scaledOdds: HashMap<String, Competitor> = HashMap(scaleOdds(initialOdds))
    fun containsId(oddsId: String): Boolean {
        return scaledOdds.containsKey(oddsId)
    }

    fun getOddsWithNames(): Map<String, Competitor> {
        return scaledOdds.toMap()
    }

    fun getOdds(): Map<String, Int> {
        return scaledOdds.mapValues { it.value.odds }
    }

    private fun scaleOdds(initialOdds: Map<String, Competitor>): Map<String, Competitor> {
        var summedProbabilities: Float = 0f
        initialOdds.values.forEach {
            summedProbabilities += 1 / (it.odds/100f)
        }
        return initialOdds.mapValues { Competitor(it.value.name, floor(it.value.odds * summedProbabilities).toInt()) }
    }
}
