import java.util.Date

sealed class Action {
    data class MatchNew(val matchId: String, val odds: Odds, val startDate: Date): Action()
    data class MatchStart(val matchId: String): Action()
    data class MatchEnd(val matchId: String): Action()
    data class PlayerJoin(val playerId: String): Action()
    data class PlayerQuit(val playerId: String): Action()
    data class Bet(val matchId: String, val playerId: String, val oddsId: String): Action()
    data class WithdrawBet(val matchId: String, val playerId: String): Action()
}