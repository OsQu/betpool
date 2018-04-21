data class Competitor(val name: String, val odds: Float)

class Odds(initialOdds: Map<String, Competitor>) {
    private val scaledOdds: HashMap<String, Competitor> = HashMap(initialOdds)
    fun containsId(oddsId: String): Boolean {
        return scaledOdds.containsKey(oddsId)
    }
    // fun scaleOdds
}
