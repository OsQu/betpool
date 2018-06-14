package betpool

class Betpool {
    private var winnings = Winnings()
    private var currentPlayers: Set<String> = setOf()
    private var matches: Matches = Matches()
    val playerNames: HashMap<String, String> = HashMap(mapOf())

    fun getCurrentPlayers(): Set<String> {
        return currentPlayers.toSet()
    }

    fun applyActions(actions: List<Action>) {
        actions.forEach { applyAction(it) }
    }

    fun applyAction(action: Action) {
        when(action) {
            is Action.PlayerJoin -> applyAction(action)
            is Action.PlayerQuit -> applyAction(action)
            is Action.MatchNew -> applyAction(action)
            is Action.MatchStart -> applyAction(action)
            is Action.MatchEnd -> applyAction(action)
            is Action.Bet -> applyAction(action)
            is Action.WithdrawBet -> applyAction(action)
        }
    }

    fun applyAction(action: Action.PlayerJoin) {
        if (currentPlayers.contains(action.playerId)) {
            throw IllegalArgumentException("Player already exists")
        } else {
            currentPlayers = currentPlayers.plus(action.playerId)
            playerNames[action.playerId] = action.playerName
        }
    }

    fun applyAction(action: Action.PlayerQuit) {
        if (!currentPlayers.contains(action.playerId)) {
            throw IllegalArgumentException("Player doesn't exists")
        } else {
            matches
                    .getMatches()
                    .filterValues { !it.isStarted() &&  it.hasPlayerBet(action.playerId)}
                    .forEach { it.value.removeBet(action.playerId) }
            currentPlayers = currentPlayers.minus(action.playerId)
        }
    }

    fun applyAction(action: Action.MatchNew) {
        matches.newMatch(action)
    }

    fun applyAction(action: Action.Bet) {
        if (!currentPlayers.contains(action.playerId)) {
            throw IllegalArgumentException("Player must be in the current pool in order to bet")
        } else {
            matches.addBet(action)
        }
    }

    fun applyAction(action: Action.WithdrawBet) {
        matches.withdrawBet(action)
    }

    fun applyAction(action: Action.MatchStart) {
        matches.startMatch(action, currentPlayers)
    }

    fun applyAction(action: Action.MatchEnd) {
        matches.endMatch(action)
        winnings = winnings.merge(matches.getMatches()[action.matchId]!!.getWinnings()!!)
    }

    fun getMatches(): Map<String, Match> {
        return matches.getMatches().toMap()
    }

    fun getWinnings(): Map<String, Int> {
        return winnings.getData();
    }
}
