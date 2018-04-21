import org.amshove.kluent.shouldEqual
import org.amshove.kluent.shouldContainAll
import org.amshove.kluent.shouldThrow
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.it
import java.util.Date

class BetpoolSpec : Spek({
    fun createOdds(): Odds {
        return Odds(
                mapOf("oddsId1" to Competitor(name = "Ronnie", odds = 1.5f), "oddsId2" to Competitor(name = "Selby", odds = 2f))
        )
    }
    it("PlayerJoin action adds a player to the pool") {
        val betpool = Betpool()
        betpool.applyAction(Action.PlayerJoin("first"))
        betpool.getCurrentPlayers() shouldEqual setOf("first")
    }

    it("PlayerQuit action remove a player from the pool") {
        val betpool = Betpool()
        betpool.applyAction(Action.PlayerJoin("first"))
        betpool.applyAction(Action.PlayerJoin("second"))
        betpool.getCurrentPlayers() shouldEqual setOf("first", "second")
        betpool.applyAction(Action.PlayerQuit("first"))
        betpool.getCurrentPlayers() shouldEqual setOf("second")
    }

    it("MatchNew action adds a match") {
        val betpool = Betpool()
        betpool.applyAction(Action.MatchNew(matchId = "testId", odds = createOdds(), startDate = Date()))
        betpool.getMatches().keys shouldContainAll listOf("testId")
    }

    it("MatchStart starts match") {
        val betpool = Betpool()
        betpool.applyAction(Action.MatchNew(matchId = "testId", odds = createOdds(), startDate = Date()))
        betpool.applyAction(Action.MatchStart(matchId = "testId"))
        betpool.getMatches()
    }
})