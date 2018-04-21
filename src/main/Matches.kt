class Matches {
    private var matches: HashMap<String, Match> = HashMap(mapOf())

    fun newMatch(action: Action.MatchNew) {
        if (matches.containsKey(action.matchId)) {
            throw IllegalArgumentException("Match for the id already exists")
        } else {
            matches[action.matchId] = Match(action.matchId, action.odds, action.startDate)
        }
    }

    fun addBet(action: Action.Bet) {
        if (!matches.containsKey(action.matchId)) {
            throw IllegalArgumentException("matchId doesn't exist")
        } else {
            matches[action.matchId]?.addBet(action.playerId, action.oddsId)
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
            matches[action.matchId]?.start(pool)
        }
    }

    fun endMatch(action: Action.MatchEnd, pool: Set<String>): Winnings {
        if (!matches.containsKey(action.matchId)) {
            throw IllegalArgumentException("matchId doesn't exist")
        } else {
            return matches[action.matchId]!!.end("oddsId1", pool)
        }
    }

    fun getMatches(): Map<String, Match> {
        return matches.toMap()
    }
}
