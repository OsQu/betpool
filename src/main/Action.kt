import java.util.Date
import kotlin.reflect.KClass

class ActionType(private val type: String) {
    enum class Type {
        PLAYER_JOIN,
        PLAYER_QUIT,
        MATCH_NEW,
        MATCH_START,
        MATCH_END,
        BET,
        WITHDRAW_BET
    }
    fun toActionClass(): KClass<out Action> {
        return when(Type.valueOf(type)) {
            Type.PLAYER_JOIN -> Action.PlayerJoin::class
            Type.PLAYER_QUIT -> Action.PlayerQuit::class
            Type.MATCH_NEW -> Action.WithdrawBet::class
            Type.MATCH_START -> Action.MatchStart::class
            Type.MATCH_END -> Action.MatchEnd::class
            Type.BET -> Action.Bet::class
            Type.WITHDRAW_BET -> Action.Bet::class
        }
    }
}

sealed class Action(val type: ActionType.Type) {
    data class MatchNew(val matchId: String, val odds: Odds, val startDate: Date): Action(ActionType.Type.MATCH_NEW)
    data class MatchStart(val matchId: String): Action(ActionType.Type.MATCH_START)
    data class MatchEnd(val matchId: String, val winner: String): Action(ActionType.Type.MATCH_END)
    data class PlayerJoin(val playerId: String): Action(ActionType.Type.PLAYER_JOIN)
    data class PlayerQuit(val playerId: String): Action(ActionType.Type.PLAYER_QUIT)
    data class Bet(val matchId: String, val playerId: String, val oddsId: String): Action(ActionType.Type.BET)
    data class WithdrawBet(val matchId: String, val playerId: String): Action(ActionType.Type.WITHDRAW_BET)
}