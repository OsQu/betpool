import betfair.MarketsAPI
import org.jooby.Jooby.*
import org.jooby.Kooby

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
    get("state") {
        val name = param("name").value("Kotlin")
        "Hello $name!"
        State.betpool.getCurrentPlayers()
    }

})

fun applyAction(action: Action) {
    synchronized(State, {
        State.betpool.applyAction(action)
    })
    State.persistence.logAction(action)
}

fun main(args: Array<String>) {
    State.betpool.applyActions(State.persistence.readActions())

    val markets = MarketsAPI.fetch()

    run(::App, args)
}
