package betpool

import org.amshove.kluent.shouldEqual
import org.amshove.kluent.shouldContainAll
import org.amshove.kluent.shouldThrow
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.it
import java.time.Instant

class MatchesSpec : Spek({
    fun createOdds(): Odds {
        return Odds(
                mapOf("oddsId1" to Competitor(name = "Ronnie", odds = 150), "oddsId2" to Competitor(name = "Selby", odds = 200))
        )
    }
    fun newMatchAction(): Action.MatchNew {
        return Action.MatchNew(matchId = "testId", matchName = "Ronnie vs. Selby", odds = createOdds(), startDate = Instant.now())
    }
    it("MatchNew action adds a match") {
        val matches = Matches()
        matches.newMatch(newMatchAction())
        matches.getMatches().keys shouldContainAll listOf("testId")
    }

    it("MatchNew throws if matchId exists") {
        val matches = Matches()
        matches.newMatch(newMatchAction())
        val func = {
            matches.newMatch(newMatchAction())
        }
        func shouldThrow IllegalArgumentException::class
    }

    it("MatchStart starts match") {
        val matches = Matches()
        matches.newMatch(newMatchAction())
        matches.startMatch(Action.MatchStart(matchId = "testId"), setOf("p1", "p2"))
        matches.getMatches()
    }

    it("MatchStart throws is matchId doesn't exist") {
        val matches = Matches()
        val func = { matches.startMatch(Action.MatchStart(matchId = "testId"), setOf("p1", "p2")) }
        func shouldThrow IllegalArgumentException::class
    }

    it("MatchStart starts the match") {
        val matches = Matches()
        matches.newMatch(newMatchAction())
        matches.startMatch(Action.MatchStart(matchId = "testId"), setOf("p1", "p2"))
        matches.getMatches()["testId"]?.isStarted() shouldEqual true
    }

    it("MatchEnd ends the match") {
        val matches = Matches()
        matches.newMatch(newMatchAction())
        matches.startMatch(Action.MatchStart(matchId = "testId"), setOf("p1", "p2"))
        matches.endMatch(Action.MatchEnd(matchId = "testId", winner = "oddsId1"))
        matches.getMatches()["testId"]?.hasEnded() shouldEqual true
    }

    it("MatchEnd throws if match doesn't exist") {
        val matches = Matches()
        val func = { matches.endMatch(Action.MatchEnd(matchId = "testId", winner = "oddsId1")) }
        func shouldThrow IllegalArgumentException::class
    }
})
