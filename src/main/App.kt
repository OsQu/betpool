import betfair.MarketsAPI
import flowdock.FlowdockAPI
import flowdock.model.Activity
import flowdock.model.Author
import flowdock.model.Thread
import org.jooby.Jooby.*
import org.jooby.Kooby
import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.TimeUnit

const val UPDATE_RATE = 1L

val UPDATE_TYPE = TimeUnit.MINUTES
val FLOW_TOKEN = System.getenv("FLOW_TOKEN") ?: throw Exception("FLOW_TOKEN not defined")

object State {
    val persistence = Persistence("/tmp/betpool.log")
    val betpool = Betpool()
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
        State.persistence.logAction(action)
    })
}

fun main(args: Array<String>) {
    State.betpool.applyActions(State.persistence.readActions())
    val scheduledExecutorPool = ScheduledThreadPoolExecutor(1)
    scheduledExecutorPool.scheduleAtFixedRate(::updateThreads, 0, UPDATE_RATE, UPDATE_TYPE)
    run(::App, args)
}

fun updateThreads() {
    println("HERE")
    val markets = MarketsAPI.fetch()
    FlowdockAPI(FLOW_TOKEN).createActivity(
        Activity(
            "Testing",
            "UU JEA",
            Author("Bet pool guy"),
            "abcde",
            flowdock.model.Thread(
                "Some betting"
            )
        )
    )
}
