import betfair.Market
import betfair.MarketsAPI
import betpool.*
import com.squareup.moshi.Moshi
import flowdock.FlowdockAPI
import flowdock.model.IncomingUpdateAction
import org.jooby.Jooby.*
import org.jooby.Kooby
import org.jooby.json.Jackson
import java.time.Duration
import java.time.Instant
import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.TimeUnit

const val UPDATE_RATE = 1L

val UPDATE_TYPE = TimeUnit.MINUTES
val FLOW_TOKEN = System.getenv("FLOW_TOKEN") ?: ""
val WEB_URL = System.getenv("WEB_URL") ?: ""
val persistence = Persistence(System.getenv("LOG_FILE") ?: "/tmp/production.log")

object State {
    val betpool = Betpool()
}

class App : Kooby({
    use(Jackson())
    get("market") {
        MarketsAPI.fetch()
    }
    get("state") {
        State.betpool.getWinnings()
    }
    post("join") { req ->
        val action = Moshi.Builder().build().adapter(IncomingUpdateAction::class.java).fromJson(req.body().value())!!
        applyAction(Action.PlayerJoin(playerId = action.agent.url, playerName = action.agent.name))
        ""
    }.consumes("json")
    post("quit") { req ->
        val action = Moshi.Builder().build().adapter(IncomingUpdateAction::class.java).fromJson(req.body().value())!!
        applyAction(Action.PlayerQuit(playerId = action.agent.url))
        ""
    }.consumes("json")
    post("/match/:matchId/bet/:oddsId") { req ->
        val action = Moshi.Builder().build().adapter(IncomingUpdateAction::class.java).fromJson(req.body().value())!!
        applyAction(Action.Bet(playerId = action.agent.url, matchId = req.param("matchId").value(), oddsId = req.param("oddsId").value()))
        ""
    }
    post("/match/:matchId/withdraw") { req ->
        val action = Moshi.Builder().build().adapter(IncomingUpdateAction::class.java).fromJson(req.body().value())!!
        applyAction(Action.WithdrawBet(playerId = action.agent.url, matchId = req.param("matchId").value()))
        ""
    }
    post("/match/:matchId/end/:winnerId") { req ->
        applyAction(Action.MatchEnd(matchId = req.param("matchId").value(), winner = req.param("winnerId").value()))
        ""
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
    scheduledExecutorPool.scheduleAtFixedRate(::scheduledUpdate, 0, UPDATE_RATE, UPDATE_TYPE)
    run(::App, args)
}

fun scheduledUpdate() {
    updateStartedMatches()
    updateFromMarketData()
}

fun updateStartedMatches() {
    State.betpool.getMatches()
            .filter { !it.value.isStarted() }
            .filter { it.value.startDate < Instant.now() }
            .forEach { applyAction(Action.MatchStart(matchId = it.key)) }
}


fun updateFromMarketData() {
    MarketsAPI.fetch()
            .filter { it.startTime < Instant.now().plus(Duration.ofHours(12)) }
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
            startDate = Instant.from(event.startTime),
            odds = odds
    )
}

fun updateFlowdock(action: Action) {
    val activities = FlowdockInfo(WEB_URL, State.betpool).flowdockActivities(action)
    activities.forEach { FlowdockAPI(FLOW_TOKEN).createActivity(it) }
}
