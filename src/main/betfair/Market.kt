package betfair

data class Market(
    val marketId: String,
    val event: String,
    val start: String, // TODO: Turne to Time
    val odds: Map<String, Runner> // TODO: Try Pair<Int, Int>
)

data class Runner(
    val name: String,
    val odds: Double
)