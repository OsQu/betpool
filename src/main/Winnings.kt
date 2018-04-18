class Winnings(private var data: HashMap<String, Int> = HashMap()) {
    init {
        if (data.values.sum() != 0) {
            throw IllegalArgumentException("Values must sum to 0")
        }
    }

    fun addPlayer(playerId: String) {
       data[playerId] = 0
    }

    fun merge(winnings: Winnings) {
        val newData = HashMap(data)
        for ((key, value) in winnings.getData()) {
            newData[key] = data.getOrDefault(key, 0) + value
        }
        data = newData
    }

    fun getData(): Map<String, Int> {
        return data.toMap()
    }
}