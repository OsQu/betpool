import betpool.Action
import betpool.Betpool
import flowdock.model.Activity
import flowdock.model.Author

class FlowdockInfo(val betpool: Betpool) {
    fun flowdockActivity(action: Action): Activity {
        return Activity(
                title = activityTitle(action),
                author = getAuthor(),
                external_thread_id = getThreadId(action),
                thread = getThread(action)
        )
    }

    private fun activityTitle(action: Action): String {
        return when(action) {
            is Action.PlayerJoin -> "joined the pool"
            is Action.PlayerQuit -> "left the pool"
            is Action.Bet -> {
                val match = betpool.getMatches()[action.matchId]!!
                "bet ${match.getOdds().getOddsWithNames()[action.oddsId]}"
            }
            is Action.WithdrawBet -> "withdrew bet"
            is Action.MatchNew -> "New match: $action.matchName opened for betting"
            is Action.MatchStart -> "Match play started"
            is Action.MatchEnd -> {
                val match = betpool.getMatches()[action.matchId]!!
                "Match ended - ${match.getOdds().getOddsWithNames()[action.winner]} won"
            }
        }
    }

    private fun getThreadId(action: Action): String {
        return when(action) {
            is Action.PlayerJoin -> "main"
            is Action.PlayerQuit -> "main"
            is Action.Bet -> action.matchId
            is Action.WithdrawBet -> action.matchId
            is Action.MatchNew -> action.matchId
            is Action.MatchStart -> action.matchId
            is Action.MatchEnd -> action.matchId
        }
    }

    private fun getThread(action: Action): flowdock.model.Thread {
        return when(action) {
            is Action.PlayerJoin -> getMainThread()
            is Action.PlayerQuit -> getMainThread()
            is Action.Bet -> getMatchThread(action.matchId)
            is Action.WithdrawBet -> getMatchThread(action.matchId)
            is Action.MatchNew -> getMatchThread(action.matchId)
            is Action.MatchStart -> getMatchThread(action.matchId)
            is Action.MatchEnd -> getMatchThread(action.matchId)
        }
    }

    private fun getMainThread(): flowdock.model.Thread {
        return flowdock.model.Thread(
                title = "Snooker World Championships 2018",
                fields = getCurrentWinningsForFlowdock(),
                actions = listOf(
                        flowdock.model.UpdateAction(name = "Join pool", target = flowdock.model.UpdateAction.Target("", "")),
                        flowdock.model.UpdateAction(name = "Quit pool", target = flowdock.model.UpdateAction.Target("", ""))
                )
        )
    }

    private fun getCurrentWinningsForFlowdock(): Map<String, String> {
        return betpool.getWinnings()
                .mapKeys { betpool.playerNames[it.key]!! }
                .mapValues { (it.value / 100).toString() }
    }

    private fun getMatchThread(matchId: String): flowdock.model.Thread {
        // TODO: get the info properly
        return flowdock.model.Thread(
                title = "Match specific thread - get the info somewhere",
                fields = mapOf("athlete1" to "odds1", "athlete2" to "odds2"),
                actions = listOf(
                        flowdock.model.UpdateAction(name = "Bet 1", target = flowdock.model.UpdateAction.Target("", "")),
                        flowdock.model.UpdateAction(name = "Bet 2", target = flowdock.model.UpdateAction.Target("", "")),
                        flowdock.model.UpdateAction(name = "Withdraw bet", target = flowdock.model.UpdateAction.Target("", ""))
                )
        )
    }

    // TODO: do this properly
    private fun getAuthor(): Author {
        return Author("Betpool")
    }
}
