class Matches {
    private var matches: HashMap<String, Match> = HashMap(mapOf())

    fun newMatch(action: Action.MatchNew) {
        if (matches.containsKey(action.matchId)) {
            throw IllegalArgumentException("Match for the id already exists")
        } else {
            matches[action.matchId] = Match(action.matchId, action.athlete1Name, action.athlete2Name, action.startDate)
        }
    }

    fun addBet(action: Action.Bet) {
        if (!matches.containsKey(action.matchId)) {
            throw IllegalArgumentException("matchId doesn't exist")
        } else {
            matches[action.matchId]?.addBet(action.playerId, action.athleteNo)
        }
    }

    fun withdrawBet(action: Action.WithdrawBet) {
        if (!matches.containsKey(action.matchId)) {
            throw IllegalArgumentException("matchId doesn't exist")
        } else {
            matches[action.matchId]?.removeBet(action.playerId)
        }
    }

    fun startMatch(action: Action.MatchStart, pool: Set<String>) {
        if (!matches.containsKey(action.matchId)) {
            throw IllegalArgumentException("matchId doesn't exist")
        } else {
            matches[action.matchId]?.setPool(pool)
        }
    }

    fun getMatches(): Map<String, Match> {
        return matches.toMap()
    }

}