import betfair.MarketsAPI
import org.jooby.Jooby.*
import org.jooby.Kooby

class App : Kooby({
    get("test") {
        val name = param("name").value("Kotlin")
        "Hello $name!"
    }
})

fun main(args: Array<String>) {
    val markets = MarketsAPI.fetch()


    run(::App, args)
}
