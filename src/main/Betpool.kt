class Betpool {
    private var winnings = Winnings()
    private var currentPlayers: Set<String> = setOf()
    private var matches: HashMap<String, Match> = HashMap(mapOf())

    fun getCurrentPlayers(): Set<String> {
        return currentPlayers.toSet()
    }

    fun applyAction(action: Action.PlayerJoin) {
        currentPlayers = currentPlayers.plus(action.playerId)
    }

    fun applyAction(action: Action.PlayerQuit) {
        currentPlayers = currentPlayers.minus(action.playerId)
    }

    fun applyAction(action: Action.MatchNew) {
        if (matches.containsKey(action.matchId)) {
            throw IllegalArgumentException("Match for the id already exists")
        } else {
            matches[action.matchId] = Match(action.matchId, action.athlete1Name, action.athlete2Name, action.startDate)
        }
    }

    fun applyAction(action: Action.Bet) {
        if (!matches.containsKey(action.matchId)) {
            throw IllegalArgumentException("matchId doesn't exist")
        } else if (!currentPlayers.contains(action.playerId)) {
            throw IllegalArgumentException("Player must be in the current pool in order to bet")
        } else {
            matches[action.matchId]?.addBet(action.playerId, action.athleteNo)
        }
    }

    fun applyAction(action: Action.WithdrawBet) {
        if (!matches.containsKey(action.matchId)) {
            throw IllegalArgumentException("matchId doesn't exist")
        } else if (!currentPlayers.contains(action.playerId)) {
            throw IllegalArgumentException("Player must be in the current pool in order to bet")
        } else {
            matches[action.matchId]?.removeBet(action.playerId)
        }
    }

    fun getMatches(): Map<String, Match> {
        return matches.toMap()
    }
}
