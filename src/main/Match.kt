import java.util.*
import kotlin.collections.HashMap

class Match(val matchId: String, val athlete1Name: String, val athlete2Name: String, val startDate: Date) {
    private var pool: Set<String>? = null
    private var bets: HashMap<String, Int> = HashMap(mapOf())

    fun getPool(): Set<String>? {
        return pool?.toSet()
    }

    fun addBet(playerId: String, athleteNo: Int) {
        if (athleteNo < 1 || athleteNo > 2) {
            throw IllegalArgumentException("Only 1 or 2 allowed")
        } else {
            bets[playerId] = athleteNo;
        }
    }

    fun setPool(closedPool: Set<String>) {
        if (pool == null) {
            pool = closedPool
        } else {
            throw IncompatibleClassChangeError("Changing the betting pool is not allowed")
        }
    }

    override fun toString(): String {
        return "$athlete1Name - $athlete2Name"
    }
}
