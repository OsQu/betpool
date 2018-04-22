package betpool

import java.util.*
import kotlin.collections.HashMap

class Match(val matchId: String, private val odds: Odds, val startDate: Date) {
    private var pool: Set<String>? = null
    private var bets: HashMap<String, String> = HashMap(mapOf())
    private var ended: Boolean = false

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

    fun start(closedPool: Set<String>) {
        if (pool == null) {
            pool = closedPool
        } else {
            throw IncompatibleClassChangeError("Changing the betting pool is not allowed")
        }
    }

    fun end(winner: String): Winnings {
        if (!isStarted()) {
            throw IllegalArgumentException("Cannot end a match that has not started")
        } else if (!odds.containsId(winner)) {
            throw IllegalArgumentException("Winner is not in the match odds")
        } else {
            ended = true
            return Winnings.create(odds = odds, bets = bets, pool = pool!!, winner = winner)
        }
    }

    fun hasEnded(): Boolean {
        return ended
    }

    fun isStarted(): Boolean {
        return pool != null
    }
}
