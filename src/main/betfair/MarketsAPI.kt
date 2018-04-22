package betfair

import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.jackson.responseObject

const val URL = "http://139.59.150.87:4567"

object MarketsAPI {
    fun fetch(): List<Market> {
        val (_, _, odds) = Fuel.get(URL).responseObject<List<Market>>()
        return odds.get()
    }
}