package betfair

import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.jackson.responseObject

const val URL = "http://134.209.251.131:4567"

object MarketsAPI {
    fun fetch(): List<Market> {
        val (_, _, odds) = Fuel.get(URL).responseObject<List<Market>>()
        return odds.get()
    }

    fun fetchWinners(matchIds: List<String>): List<MarketWinner> {
        val params = listOf("market_ids" to matchIds.joinToString(","))
        val (_, _, winners) = Fuel.get("$URL/winner", params).responseObject<List<MarketWinner>>()
        return winners.get()
    }
}