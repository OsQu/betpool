import org.amshove.kluent.shouldEqual
import org.amshove.kluent.shouldContainAll
import org.amshove.kluent.shouldThrow
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.it
import java.util.Date

class MatchesSpec : Spek({
    it("MatchNew action adds a match") {
        val matches = Matches()
        matches.newMatch(Action.MatchNew(matchId = "testId", athlete1Name = "Ronnie", athlete2Name = "Selby", startDate = Date()))
        matches.getMatches().keys shouldContainAll listOf("testId")
    }

    it("MatchNew throws if matchId exists") {
        val matches = Matches()
        matches.newMatch(Action.MatchNew(matchId = "testId", athlete1Name = "Ronnie", athlete2Name = "Selby", startDate = Date()))
        val func = {
            matches.newMatch(Action.MatchNew(matchId = "testId", athlete1Name = "Selby", athlete2Name = "Ronnie", startDate = Date()))
        }
        func shouldThrow IllegalArgumentException::class
    }

    it("MatchStart starts match") {
        val matches = Matches()
        matches.newMatch(Action.MatchNew(matchId = "testId", athlete1Name = "Ronnie", athlete2Name = "Selby", startDate = Date()))
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
        matches.newMatch(Action.MatchNew(matchId = "testId", athlete1Name = "Ronnie", athlete2Name = "Selby", startDate = Date()))
        matches.startMatch(Action.MatchStart(matchId = "testId"), setOf("p1", "p2"))
        matches.getMatches()["testId"]?.isStarted() shouldEqual true
    }
})