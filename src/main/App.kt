import betfair.MarketsAPI
import betpool.Betpool
import betpool.Action
import flowdock.FlowdockAPI
import flowdock.model.Activity
import flowdock.model.Author
import org.jooby.Jooby.*
import org.jooby.Kooby
import java.util.*
import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.TimeUnit

const val UPDATE_RATE = 5L

val UPDATE_TYPE = TimeUnit.MINUTES
val FLOW_TOKEN = System.getenv("FLOW_TOKEN") ?: "" //throw Exception("FLOW_TOKEN not defined")
val persistence = Persistence(System.getenv("LOG_FILE") ?: "/tmp/betpool.log")

data class CreateAuthor(val userId: String, val name: String)

object State {
    val betpool = Betpool()
    val flowdockUsers = HashMap<String, Author>()
}

class App : Kooby({
    get("test") {
        val name = param("name").value("Kotlin")
        applyAction(Action.PlayerJoin(name))
        "Hello $name!"
    }

    get("market") {
        MarketsAPI.fetch()
    }
    get("state") {
        val name = param("name").value("Kotlin")
        "Hello $name!"
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
    val markets = MarketsAPI.fetch()
    // Do the needful actions
}

fun getCurrentWinningsForFlowdock(): Map<String, String> {
    return State.betpool.getWinnings()
            .mapKeys { State.flowdockUsers[it.key]!!.name }
            .mapValues { (it.value / 100).toString() }
}

fun activityTitle(action: Action): String {
    return when(action) {
        is Action.PlayerJoin -> "joined the pool"
        is Action.PlayerQuit -> "left the pool"
        is Action.Bet -> "bet ${action.oddsId}"
        is Action.WithdrawBet -> "withdrew bet"
        is Action.MatchNew -> "New match opened for betting"
        is Action.MatchStart -> "Match play started"
        is Action.MatchEnd -> "Match ended - ${State.betpool.getMatches()[action.matchId]!!.getWinner()} won"
    }
}

fun getThreadId(action: Action): String {
    return when(action) {
        is Action.PlayerJoin -> "main"
        is Action.PlayerQuit -> "main"
        is Action.Bet -> action.matchId
        is Action.WithdrawBet -> action.matchId
        is Action.MatchNew -> action.matchId
        is Action.MatchStart -> action.matchId
        is Action.MatchEnd -> action.matchId
    }
}

fun getThread(action: Action): flowdock.model.Thread {
    return when(action) {
        is Action.PlayerJoin -> getMainThread()
        is Action.PlayerQuit -> getMainThread()
        is Action.Bet -> getMatchThread(action.matchId)
        is Action.WithdrawBet -> getMatchThread(action.matchId)
        is Action.MatchNew -> getMatchThread(action.matchId)
        is Action.MatchStart -> getMatchThread(action.matchId)
        is Action.MatchEnd -> getMatchThread(action.matchId)
    }
}

fun getMainThread(): flowdock.model.Thread {
    return flowdock.model.Thread(
            title = "Snooker World Championships 2018",
            fields = getCurrentWinningsForFlowdock(),
            actions = listOf(
                    flowdock.model.UpdateAction(name = "Join pool", target = flowdock.model.UpdateAction.Target("", "")),
                    flowdock.model.UpdateAction(name = "Quit pool", target = flowdock.model.UpdateAction.Target("", ""))
            )
    )
}

fun getMatchThread(matchId: String): flowdock.model.Thread {
    // TODO: get the info properly
    return flowdock.model.Thread(
            title = "Match specific thread - get the info somewhere",
            fields = mapOf("athlete1" to "odds1", "athlete2" to "odds2"),
            actions = listOf(
                    flowdock.model.UpdateAction(name = "Bet 1", target = flowdock.model.UpdateAction.Target("", "")),
                    flowdock.model.UpdateAction(name = "Bet 2", target = flowdock.model.UpdateAction.Target("", "")),
                    flowdock.model.UpdateAction(name = "Withdraw bet", target = flowdock.model.UpdateAction.Target("", ""))
            )
    )
}

// TODO: do this properly
fun getAuthor(): Author {
    return Author("Betpool")
}

fun flowdockAction(action: Action): Activity {
    return Activity(
            title = activityTitle(action),
            author = getAuthor(),
            external_thread_id = getThreadId(action),
            thread = getThread(action)
    )
}

fun updateFlowdock(action: Action) {
    FlowdockAPI(FLOW_TOKEN).createActivity(flowdockAction(action))
}
