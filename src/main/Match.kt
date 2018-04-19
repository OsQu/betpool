import java.util.*

class Match(val matchId: String, val athlete1Name: String, val athlete2Name: String, val startDate: Date) {
    private var pool: Set<String>? = null


    fun getPool(): Set<String>? {
        return pool?.toSet()
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
