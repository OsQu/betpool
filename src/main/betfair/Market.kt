package betfair

import java.time.Instant

data class Market(
    val marketId: String,
    val event: String,
    private val start: String,
    val odds: Map<String, Runner>
) {
    val startTime: Instant
        get() = Instant.parse(start)
}

data class Runner(
        val name: String,
        val odds: Double
)

data class MarketWinner(val marketId: String, val winner: String?)
