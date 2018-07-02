import betfair.Market
import betfair.MarketsAPI
import betpool.*
import flowdock.FlowdockAPI
import flowdock.model.Activity
import flowdock.model.Author
import flowdock.model.IncomingUpdateAction
import org.jooby.Jooby.*
import org.jooby.Kooby
import org.jooby.Request
import org.jooby.json.Jackson
import java.time.Duration
import java.time.Instant
import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.TimeUnit

const val UPDATE_RATE = 1L
val UPDATE_TYPE = TimeUnit.MINUTES

val FLOW_TOKENS = setOf(System.getenv("FLOW_TOKEN_1") ?: "", System.getenv("FLOW_TOKEN_2") ?: "")
val WEB_URL = System.getenv("WEB_URL") ?: ""
val persistence = Persistence(System.getenv("LOG_FILE") ?: "/tmp/production.log")
val BET_SIZE = Integer.parseInt(System.getenv("BET_SIZE") ?: "10")
val scheduledExecutorPool = ScheduledThreadPoolExecutor(1)

object State {
    val betpool = Betpool()
}

fun createActionFromRequest(request: Request): IncomingUpdateAction  {
    return IncomingUpdateAction.build(request.header("Flowdock-Token").value(), request.body().value())
}

class App : Kooby({
    use(Jackson())
    get("market") {
        MarketsAPI.fetch()
    }
    get("state") {
        State.betpool.getWinnings()
                .mapKeys { State.betpool.playerNames.getOrDefault(it.key, "Unknown") }
                .mapValues { (BET_SIZE.toFloat() * it.value.toFloat() / 100f).toString() + "€" }
    }
    get("players") {
        State.betpool.getCurrentPlayers().map { State.betpool.playerNames.getOrDefault(it, "Unknown") }
    }
    post("start") {
        val activity = Activity(
                title = "Betpool started for World cup 2018!",
                author = Author(name = "Betpool"),
                thread = FlowdockInfo(WEB_URL, State.betpool).getMainThread(),
                external_thread_id = "main"
        )
        FlowdockAPI(FLOW_TOKENS).createActivity(activity)
    }
    post("join") { req ->
        val action = createActionFromRequest(req)
        applyAction(Action.PlayerJoin(playerId = action.agent.url, playerName = action.agent.name))
        ""
    }.consumes("json")
    post("quit") { req ->
        val action = createActionFromRequest(req)
        applyAction(Action.PlayerQuit(playerId = action.agent.url))
        ""
    }.consumes("json")
    post("/match/:matchId/bet/:oddsId") { req ->
        val action = createActionFromRequest(req)
        applyAction(Action.Bet(playerId = action.agent.url, matchId = req.param("matchId").value(), oddsId = req.param("oddsId").value()))
        ""
    }
    post("/match/:matchId/withdraw") { req ->
        val action = createActionFromRequest(req)
        applyAction(Action.WithdrawBet(playerId = action.agent.url, matchId = req.param("matchId").value()))
        ""
    }
    get("/queue") {
        "Active: ${scheduledExecutorPool.activeCount}, tasks completed: ${scheduledExecutorPool.taskCount}, queue: ${scheduledExecutorPool.queue.size}"
    }
})

fun applyAction(action: Action) {
    synchronized(State) {
        State.betpool.applyAction(action)
        persistence.logAction(action)
    }
    updateFlowdock(action)
}

fun main(args: Array<String>) {
    State.betpool.applyActions(persistence.readActions())
    scheduledExecutorPool.removeOnCancelPolicy = true
    scheduledExecutorPool.scheduleAtFixedRate(::scheduledUpdate, 0, UPDATE_RATE, UPDATE_TYPE)
    scheduledExecutorPool.scheduleAtFixedRate(::fetchMatchWinners, 0, 5L, UPDATE_TYPE)
    run(::App, args)
}

fun scheduledUpdate() {
    try {
        updateStartedMatches()
        updateFromMarketData()
    } catch(e: Exception) {
        println("ScheduledUpdate exception: ${e.message}")
    }
}

fun fetchMatchWinners() {
    try {
        val inProgressMatchIds = State.betpool.getMatches()
            .filter { it.value.isStarted() && !it.value.hasEnded() }
            .map { it.key }
        MarketsAPI.fetchWinners(inProgressMatchIds).forEach {
            if (it.winner != null) {
                applyAction(Action.MatchEnd(matchId = it.marketId, winner = it.winner))
            }
        }
    } catch(e: Exception) {
        println("fetchMatchWinners exception: ${e.message}")
    }
}

fun updateStartedMatches() {
    State.betpool.getMatches()
            .filter { !it.value.isStarted() }
            .filter { it.value.startDate < Instant.now() }
            .forEach { applyAction(Action.MatchStart(matchId = it.key)) }
}


fun updateFromMarketData() {
    MarketsAPI.fetch()
            .filter { it.startTime < Instant.now().plus(Duration.ofHours(24)) }
            .filter { !State.betpool.getMatches().containsKey(it.marketId) }
            .forEach { applyAction(createNewMatchActionFromMarketEvent(it)) }
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
    activities.forEach { FlowdockAPI(FLOW_TOKENS).createActivity(it) }
}
