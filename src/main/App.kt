import betfair.Market
import betfair.MarketsAPI
import betpool.*
import flowdock.FlowdockAPI
import org.jooby.Jooby.*
import org.jooby.Kooby
import java.time.Duration
import java.time.Instant
import java.util.*
import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.TimeUnit

const val UPDATE_RATE = 1L

val UPDATE_TYPE = TimeUnit.MINUTES
val FLOW_TOKEN = System.getenv("FLOW_TOKEN") ?: ""
val persistence = Persistence(System.getenv("LOG_FILE") ?: "/tmp/betpool.log")

object State {
    val betpool = Betpool()
}

class App : Kooby({
    get("test") {
        val name = param("name").value("Kotlin")
        applyAction(Action.PlayerJoin(playerId = name, playerName = name))
        "Hello $name!"
    }

    get("market") {
        MarketsAPI.fetch()
    }
    get("fetchmarkets") {
        updateFromMarketData()
        MarketsAPI.fetch()
    }
    get("state") {
        val name = param("name").value("Kotlin")
        State.betpool.getCurrentPlayers()
    }
})

fun applyAction(action: Action) {
    synchronized(State, {
        State.betpool.applyAction(action)
        persistence.logAction(action)
    })
    updateFlowdock(action)
}

fun main(args: Array<String>) {
    State.betpool.applyActions(persistence.readActions())
    val scheduledExecutorPool = ScheduledThreadPoolExecutor(1)
    scheduledExecutorPool.scheduleAtFixedRate(::updateFromMarketData, 0, UPDATE_RATE, UPDATE_TYPE)
    run(::App, args)
}

fun updateFromMarketData() {
    MarketsAPI.fetch()
            .filter { it.startTime < Instant.now().plus(Duration.ofHours(24)) }
            .filter { !State.betpool.getMatches().containsKey(it.marketId) }
            .forEach({ applyAction(createNewMatchActionFromMarketEvent(it)) })
}

fun createNewMatchActionFromMarketEvent(event: Market): Action.MatchNew {
    val odds = Odds(
            event.odds.mapValues { Competitor(name = it.value.name, odds = (it.value.odds * 100).toInt()) }
    )
    return Action.MatchNew(
            matchId = event.marketId,
            matchName = event.event,
            startDate = Date.from(event.startTime),
            odds = odds
    )
}

fun updateFlowdock(action: Action) {
    FlowdockAPI(FLOW_TOKEN).createActivity(FlowdockInfo("http://139.59.150.87", State.betpool).flowdockActivity(action))
}
