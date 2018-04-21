data class Competitor(val name: String, val odds: Int)

class Odds(initialOdds: Map<String, Competitor>) {
    private val scaledOdds: HashMap<String, Competitor> = HashMap(initialOdds)
    fun containsId(oddsId: String): Boolean {
        return scaledOdds.containsKey(oddsId)
    }

    fun getOddsWithNames(): Map<String, Competitor> {
        return scaledOdds.toMap()
    }

    fun getOdds(): Map<String, Int> {
        return scaledOdds.mapValues { it.value.odds }
    }
    // fun scaleOdds
}
