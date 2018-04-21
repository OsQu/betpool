import java.util.*
import kotlin.collections.HashMap

class Match(val matchId: String, val odds: Odds, val startDate: Date) {
    private var pool: Set<String>? = null
    private var bets: HashMap<String, String> = HashMap(mapOf())

    fun getPool(): Set<String>? {
        return pool?.toSet()
    }

    fun getBets(): Map<String, String> {
        return bets.toMap()
    }

    fun addBet(playerId: String, oddsId: String) {
        if (pool != null) {
            throw IncompatibleClassChangeError("Betting is closed")
        } else if (!odds.containsId(oddsId)) {
            throw IllegalArgumentException("OddsId doesn't exist on this match")
        } else if (bets.containsKey(playerId)) {
            throw IllegalArgumentException("Player has already bet on this match")
        } else {
            bets[playerId] = oddsId;
        }
    }

    fun removeBet(playerId: String) {
        if (pool != null) {
            throw IncompatibleClassChangeError("Betting is closed")
        } else if (bets.containsKey(playerId)) {
            bets.remove(playerId);
        } else {
            throw IllegalArgumentException("Player has not bet to this match")
        }
    }

    fun setPool(closedPool: Set<String>) {
        if (pool == null) {
            pool = closedPool
        } else {
            throw IncompatibleClassChangeError("Changing the betting pool is not allowed")
        }
    }

    fun isStarted(): Boolean {
        return pool != null
    }
}
