package betpool

import java.time.Instant
import kotlin.collections.HashMap

class Match(val matchName: String, private val odds: Odds, val startDate: Instant) {
    private var pool: Set<String>? = null
    private var bets: HashMap<String, String> = HashMap(mapOf())
    private var winner: String? = null
    private var winnings: Winnings? = null

    fun getPool(): Set<String>? {
        return pool?.toSet()
    }

    fun getBets(): Map<String, String> {
        return bets.toMap()
    }

    fun getWinner(): String {
        return winner!!
    }

    fun getOdds(): Odds {
        return odds
    }

    fun addBet(playerId: String, oddsId: String) {
        if (pool != null) {
            throw IllegalStateException("Betting is closed")
        } else if (!odds.containsId(oddsId)) {
            throw IllegalArgumentException("OddsId $oddsId doesn't exist on this match")
        } else if (bets.containsKey(playerId)) {
            throw IllegalStateException("Player has already bet on this match")
        } else {
            bets[playerId] = oddsId;
        }
    }

    fun removeBet(playerId: String) {
        if (pool != null) {
            throw IllegalStateException("Betting is closed")
        } else if (bets.containsKey(playerId)) {
            bets.remove(playerId);
        } else {
            throw IllegalStateException("Player has not bet to this match")
        }
    }

    fun start(closedPool: Set<String>) {
        if (pool == null) {
            pool = closedPool
        } else {
            throw IllegalStateException("Match has already started")
        }
    }

    fun end(matchWinner: String) {
        if (!isStarted()) {
            throw IllegalStateException("Cannot end a match that has not started")
        } else if (!odds.containsId(matchWinner)) {
            throw IllegalArgumentException("Winner is not in the match odds")
        } else if (hasEnded()) {
          throw  IllegalStateException("Match has already ended")
        } else {
            winner = matchWinner
            winnings = Winnings.create(odds = odds, bets = bets, pool = pool!!, winner = matchWinner)
        }
    }

    fun getWinnings(): Winnings? {
        return winnings
    }

    fun hasEnded(): Boolean {
        return !winner.isNullOrEmpty()
    }

    fun isStarted(): Boolean {
        return pool != null
    }
}
