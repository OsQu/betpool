class Betpool {
    private var winnings = Winnings()
    private var currentPlayers: Set<String> = setOf()
    private var matches: Matches = Matches()

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
        winnings = winnings.merge(matches.endMatch(action))
    }

    fun getMatches(): Map<String, Match> {
        return matches.getMatches().toMap()
    }

    fun getWinnings(): Map<String, Int> {
        return winnings.getData();
    }
}
