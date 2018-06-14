package betpool

import org.amshove.kluent.shouldContainAll
import org.amshove.kluent.shouldEqual
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.it
import java.time.Instant

class BetpoolSpec : Spek({
    fun createOdds(): Odds {
        return Odds(
                mapOf("oddsId1" to Competitor(name = "Ronnie", odds = 150), "oddsId2" to Competitor(name = "Selby", odds = 200))
        )
    }
    fun newMatchAction(): Action.MatchNew {
        return Action.MatchNew(matchId = "testId", matchName = "Ronnie vs. Selby", odds = createOdds(), startDate = Instant.now())
    }
    it("PlayerJoin action adds a player to the pool") {
        val betpool = Betpool()
        betpool.applyAction(Action.PlayerJoin("first", "Sampo"))
        betpool.getCurrentPlayers() shouldEqual  setOf("first")
    }

    it("PlayerQuit action remove a player from the pool") {
        val betpool = Betpool()
        betpool.applyAction(Action.PlayerJoin("first", "Sampo"))
        betpool.applyAction(Action.PlayerJoin("second", "Oskari"))
        betpool.getCurrentPlayers() shouldEqual setOf("first", "second")
        betpool.applyAction(Action.PlayerQuit("first"))
        betpool.getCurrentPlayers() shouldEqual setOf("second")
    }

    it("PlayerQuit removes player's unstarted bets") {
        val betpool = Betpool()
        betpool.applyAction(Action.PlayerJoin("first", "Sampo"))
        betpool.applyAction(newMatchAction())
        betpool.applyAction(Action.MatchNew(matchId = "testId2", matchName = "Ronnie vs. Selby", odds = createOdds(), startDate = Instant.now()))
        betpool.applyAction(Action.Bet(playerId = "first", matchId = "testId", oddsId = "oddsId1"))
        betpool.applyAction(Action.Bet(playerId = "first", matchId = "testId2", oddsId = "oddsId1"))
        betpool.applyAction(Action.MatchStart("testId2"))
        betpool.applyAction(Action.PlayerQuit("first"))
        betpool.getMatches()["testId"]!!.getBets().containsKey("first") shouldEqual false
        betpool.getMatches()["testId2"]!!.getBets().containsKey("first") shouldEqual true
    }

    it("MatchNew action adds a match") {
        val betpool = Betpool()
        betpool.applyAction(newMatchAction())
        betpool.getMatches().keys shouldContainAll listOf("testId")
    }

    it("MatchStart starts match") {
        val betpool = Betpool()
        betpool.applyAction(newMatchAction())
        betpool.applyAction(Action.MatchStart(matchId = "testId"))
        betpool.getMatches()
    }

    it("Bet bets a match") {
        val betpool = Betpool()
        betpool.applyAction(newMatchAction())
        betpool.applyAction(Action.PlayerJoin("first", "Sampo"))
        betpool.applyAction(Action.Bet(playerId = "first", matchId = "testId", oddsId = "oddsId1"))
        betpool.getMatches()["testId"]!!.getBets()["first"] shouldEqual "oddsId1"
    }

    it("WithdrawBet withdraws a bet") {
        val betpool = Betpool()
        betpool.applyAction(newMatchAction())
        betpool.applyAction(Action.PlayerJoin("first", "Sampo"))
        betpool.applyAction(Action.Bet(playerId = "first", matchId = "testId", oddsId = "oddsId1"))
        betpool.applyAction(Action.WithdrawBet(playerId = "first", matchId = "testId"))
        betpool.getMatches()["testId"]!!.getBets().containsKey("first") shouldEqual false
    }

    it("MatchEnd ends a match") {
        val betpool = Betpool()
        betpool.applyAction(newMatchAction())
        betpool.applyAction(Action.MatchStart(matchId = "testId"))
        betpool.applyAction(Action.MatchEnd(matchId = "testId", winner = "oddsId1"))
        betpool.getMatches()["testId"]!!.hasEnded() shouldEqual true
    }

    it("MatchEnd merges the match winnings") {
        val betpool = Betpool()
        betpool.applyAction(newMatchAction())
        betpool.applyAction(Action.PlayerJoin("first", "Sampo"))
        betpool.applyAction(Action.PlayerJoin("second", "Oskari"))
        betpool.applyAction(Action.Bet(playerId = "first", matchId = "testId", oddsId = "oddsId1"))
        betpool.applyAction(Action.MatchStart(matchId = "testId"))
        betpool.applyAction(Action.MatchEnd(matchId = "testId", winner = "oddsId1"))
        betpool.getWinnings() shouldEqual mapOf("first" to 37, "second" to -37)
        betpool.applyAction(Action.MatchNew(matchId = "testId2", matchName = "Second match", odds = createOdds(), startDate = Instant.now()))
        betpool.applyAction(Action.Bet(playerId = "first", matchId = "testId2", oddsId = "oddsId1"))
        betpool.applyAction(Action.MatchStart(matchId = "testId2"))
        betpool.applyAction(Action.MatchEnd(matchId = "testId2", winner = "oddsId1"))
        betpool.getWinnings() shouldEqual mapOf("first" to 74, "second" to -74)
    }
})
