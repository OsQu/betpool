package betpool

import java.util.Date
import kotlin.reflect.KClass
import com.squareup.moshi.Moshi

data class ActionData(val type: String)

sealed class Action(val type: Type) {
    enum class Type {
        PLAYER_JOIN,
        PLAYER_QUIT,
        MATCH_NEW,
        MATCH_START,
        MATCH_END,
        BET,
        WITHDRAW_BET
    }
    companion object {
        fun fromJSON(jsonString: String): Action {
            val moshi = Moshi.Builder().add(Persistence.DateAdapter()).build()
            val actionData = moshi.adapter(ActionData::class.java).fromJson(jsonString)!!
            return moshi.adapter(toActionClass(actionData.type).java).fromJson(jsonString)!!
        }

        private fun toActionClass(typeStr: String): KClass<out Action> {
            return when(Type.valueOf(typeStr)) {
                Type.PLAYER_JOIN -> PlayerJoin::class
                Type.PLAYER_QUIT -> PlayerQuit::class
                Type.MATCH_NEW -> MatchNew::class
                Type.MATCH_START -> MatchStart::class
                Type.MATCH_END -> MatchEnd::class
                Type.BET -> Bet::class
                Type.WITHDRAW_BET -> WithdrawBet::class
            }
        }
    }
    data class MatchNew(val matchId: String, val matchName: String, val odds: Odds, val startDate: Date): Action(Type.MATCH_NEW)
    data class MatchStart(val matchId: String): Action(Type.MATCH_START)
    data class MatchEnd(val matchId: String, val winner: String): Action(Type.MATCH_END)
    data class PlayerJoin(val playerId: String, val playerName: String): Action(Type.PLAYER_JOIN)
    data class PlayerQuit(val playerId: String): Action(Type.PLAYER_QUIT)
    data class Bet(val matchId: String, val playerId: String, val oddsId: String): Action(Type.BET)
    data class WithdrawBet(val matchId: String, val playerId: String): Action(Type.WITHDRAW_BET)
}