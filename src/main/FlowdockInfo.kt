import betpool.Action
import betpool.Betpool
import betpool.Winnings
import flowdock.model.Activity
import flowdock.model.Author
import flowdock.model.Field
import flowdock.model.Thread
import flowdock.model.UpdateAction

class FlowdockInfo(private val actionUrl: String, val betpool: Betpool) {
    fun flowdockActivities(action: Action): List<Activity> {
        var activities = listOf(Activity(
                title = activityTitle(action),
                author = getAuthor(action),
                external_thread_id = getThreadId(action),
                thread = getThread(action),
                body = activityBody(action)
        ))
        if (action is Action.MatchEnd) {
            activities = activities.plus(dealWinningsActivity(action))
        }
        return activities
    }

    private fun betpoolIntToString(value: Int): String {
        return (value.toFloat() / 100).toString()
    }

    private fun winningsToString(winnings: Winnings): String {
        return winnings.getData().map { "${betpool.playerNames[it.key]}: ${betpoolIntToString(it.value)}"}.joinToString(", ")
    }

    private fun dealWinningsActivity(action: Action.MatchEnd): Activity {
        val match = betpool.getMatches()[action.matchId]!!
        val body = winningsToString(match.getWinnings()!!)
        return Activity(
                title = "Winnings dealt from match ${match.matchName}",
                body = body,
                author = Author(name = "Betpool"),
                external_thread_id = "main",
                thread = getMainThread()
        )
    }

    private fun activityBody(action: Action): String? {
        return when(action) {
            is Action.PlayerJoin -> null
            is Action.PlayerQuit -> null
            is Action.Bet -> null
            is Action.WithdrawBet -> null
            is Action.MatchNew -> null
            is Action.MatchStart -> null
            is Action.MatchEnd -> {
                val match = betpool.getMatches()[action.matchId]!!
                winningsToString(match.getWinnings()!!)
            }
        }
    }

    private fun activityTitle(action: Action): String {
        return when(action) {
            is Action.PlayerJoin -> "joined the pool"
            is Action.PlayerQuit -> "left the pool"
            is Action.Bet -> {
                val match = betpool.getMatches()[action.matchId]!!
                "bet ${match.getOdds().getOddsWithNames()[action.oddsId]!!.name}"
            }
            is Action.WithdrawBet -> "withdrew bet"
            is Action.MatchNew -> "New match: ${action.matchName} opened for betting"
            is Action.MatchStart -> "Match play started"
            is Action.MatchEnd -> {
                val match = betpool.getMatches()[action.matchId]!!
                "Match ended. ${match.getOdds().getOddsWithNames()[action.winner]!!.name} won"
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
                fields = getCurrentWinningsForFlowdock().map { Field(label = it.key, value = it.value) },
                actions = listOf(
                        flowdock.model.UpdateAction(
                                name = "Join pool",
                                target = UpdateAction.Target(
                                        urlTemplate = "$actionUrl/join",
                                        httpMethod = "POST"
                                )
                        ),
                        flowdock.model.UpdateAction(
                                name = "Quit pool",
                                target = UpdateAction.Target(
                                        urlTemplate = "$actionUrl/quit",
                                        httpMethod = "POST"
                                )
                        )
                )
        )
    }

    private fun getCurrentWinningsForFlowdock(): Map<String, String> {
        return betpool.getWinnings()
                .mapKeys { betpool.playerNames[it.key]!! }
                .mapValues { (it.value.toFloat() / 100f).toString() }
    }

    private fun getMatchThread(matchId: String): flowdock.model.Thread {
        val match = betpool.getMatches()[matchId]!!
        val actions = match.getOdds().getOddsWithNames().map {
            flowdock.model.UpdateAction(
                    name = "Bet for ${it.value.name}",
                    target = UpdateAction.Target(
                            urlTemplate = "$actionUrl/match/$matchId/bet/${it.key}",
                            httpMethod = "POST"
                    )
            )
        }.plus(flowdock.model.UpdateAction(name = "Withdraw bet", target = UpdateAction.Target(
                urlTemplate = "$actionUrl/match/$matchId/withdraw",
                httpMethod = "POST"
        )))
        val fields = match
                .getOdds()
                .getOddsWithNames()
                .map { Field(label = it.value.name, value = (it.value.odds.toFloat() / 100).toString()) }
        val status: Thread.Status = {
           if (match.hasEnded()) {
               Thread.Status(value = "Finished", color = "purple")
           } else if(match.isStarted()) {
               Thread.Status(value = "In play", color = "blue")
           } else {
               Thread.Status(value = "Betting", color = "green")
           }
        }()
        return flowdock.model.Thread(
                title = match.matchName,
                fields = fields,
                actions = actions,
                status = status
        )
    }

    private fun getAuthor(action: Action): Author {
        val name = when(action) {
            is Action.PlayerJoin -> action.playerName
            is Action.PlayerQuit -> betpool.playerNames[action.playerId]!!
            is Action.Bet -> betpool.playerNames[action.playerId]!!
            is Action.WithdrawBet -> betpool.playerNames[action.playerId]!!
            is Action.MatchNew -> "Betpool"
            is Action.MatchStart -> "Betpool"
            is Action.MatchEnd -> "Betpool"
        }
        return Author(name)
    }
}
