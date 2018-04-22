import kotlin.math.floor

class Winnings(private var data: HashMap<String, Int> = HashMap()) {
    init {
        if (data.values.sum() != 0) {
            throw IllegalArgumentException("Values must sum to 0")
        }
    }

    companion object Factory {
        fun create(odds: Odds, bets: HashMap<String, String>, pool: Set<String>, winner: String): Winnings {
            if (pool.isEmpty()) {
                return Winnings(HashMap())
            } else {
                val oddsValues = odds.getOdds()
                val betWinnings = bets.mapValues {
                    if (it.value == winner) {
                        oddsValues[winner]!! -100
                    } else {
                        -100
                    }
                }
                val poolResult = -betWinnings.values.sum()
                val poolResultPerPlayer: Int = floor((poolResult / pool.size).toDouble()).toInt()
                var poolResults = pool.associateBy({ it }, { poolResultPerPlayer })
                var remainder: Int = poolResult % pool.size
                val endResults = pool.plus(bets.keys).associateBy({ it }, { betWinnings.getOrDefault(it, 0) + poolResults.getOrDefault(it, 0) })
                val evenedEndResults = endResults.mapValues {
                    if (remainder > 0) {
                        remainder--
                        it.value + 1
                    } else if (remainder < 0) {
                        remainder++
                        it.value - 1
                    } else {
                        it.value
                    }
                }
                return Winnings(HashMap(evenedEndResults))
            }
        }
    }

    fun addPlayer(playerId: String) {
       data[playerId] = 0
    }

    fun merge(winnings: Winnings): Winnings {
        val newData = HashMap(data)
        for ((key, value) in winnings.getData()) {
            newData[key] = data.getOrDefault(key, 0) + value
        }
        return Winnings(newData)
    }

    fun getData(): Map<String, Int> {
        return data.toMap()
    }
}
