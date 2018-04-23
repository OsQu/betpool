import betfair.MarketsAPI
import betpool.Betpool
import betpool.Action
import flowdock.FlowdockAPI
import org.jooby.Jooby.*
import org.jooby.Kooby
import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.TimeUnit

const val UPDATE_RATE = 5L

val UPDATE_TYPE = TimeUnit.MINUTES
val FLOW_TOKEN = System.getenv("FLOW_TOKEN") ?: "" //throw Exception("FLOW_TOKEN not defined")
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
    BetUpdater(FLOW_TOKEN).run()
}

fun updateFlowdock(action: Action) {
    FlowdockAPI(FLOW_TOKEN).createActivity(FlowdockInfo(State.betpool).flowdockActivity(action))
}
