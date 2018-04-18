object Betpool {
    private var winnings = Winnings()
    private var currentPlayers: Set<String> = setOf()

    fun getCurrentPlayers(): Set<String> {
        return currentPlayers.toSet()
    }

    fun applyAction(action: Action.PlayerJoin) {
        currentPlayers = currentPlayers.plus(action.playerId)
    }

    fun applyAction(action: Action.PlayerQuit) {
        currentPlayers = currentPlayers.minus(action.playerId)
    }
}
