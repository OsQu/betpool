package betfair

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonSetter
import com.fasterxml.jackson.annotation.JsonValue
import java.time.Instant

data class Market(
    val marketId: String,
    val event: String,
    val start: String,
    val odds: Map<String, Runner>
) {
    val startTime: Instant
        get() = Instant.parse(start)
}

data class Runner(
    val name: String,
    val odds: Double
)