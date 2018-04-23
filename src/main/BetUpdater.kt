import betfair.Market
import betfair.MarketsAPI
import betfair.Runner
import flowdock.FlowdockAPI
import flowdock.model.Activity
import flowdock.model.Author
import flowdock.model.Thread
import flowdock.model.UpdateAction

typealias Odds = Map<String, Runner>
const val ACTION_URL = "http://139.59.150.87"

class BetUpdater(val flowToken: String) {
    fun run() {
        println("HERE")

        val markets = MarketsAPI.fetch()
        markets.forEach({ market ->
            FlowdockAPI(FLOW_TOKEN).createActivity(
                Activity(
                    title = "Updated betting information",
                    author = Author("Bet pool guy"),
                    external_thread_id = market.marketId,
                    thread = Thread(
                        title = market.event,
                        body = "Odds: ${renderOdds(market.odds)}",
                        actions = buildActions(market.odds)

                    )
                )
            )
        })
    }

    private fun renderOdds(odds: Odds): String =
        odds.map { (_, runner) ->
            "${runner.name} [${runner.odds}]"
        }.joinToString(" - ")

    private fun buildActions(odds: Odds): List<flowdock.model.ThreadAction> =
        odds.map { (id, runner) ->
            flowdock.model.UpdateAction(
                name = "Bet for ${runner.name}",
                target = UpdateAction.Target(
                    urlTemplate = "${ACTION_URL}/bet/${id}",
                    httpMethod = "POST"
                )
            )
        }
}