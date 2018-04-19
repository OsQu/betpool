import org.amshove.kluent.shouldEqual
import org.amshove.kluent.shouldContainAll
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.it
import java.util.Date

class BetpoolSpec : Spek({
    it("PlayerJoin action adds a player to the pool") {
        Betpool.applyAction(Action.PlayerJoin("first"))
        Betpool.getCurrentPlayers() shouldEqual setOf("first")
    }

    it("PlayerQuit action remove a player from the pool") {
        Betpool.applyAction(Action.PlayerJoin("first"))
        Betpool.applyAction(Action.PlayerJoin("second"))
        Betpool.getCurrentPlayers() shouldEqual setOf("first", "second")
        Betpool.applyAction(Action.PlayerQuit("first"))
        Betpool.getCurrentPlayers() shouldEqual setOf("second")
    }

    it("MatchNew action adds a match") {
        Betpool.applyAction(Action.MatchNew(matchId = "testId", athlete1Name = "Ronnie", athlete2Name = "Selby", startDate = Date()))
        Betpool.getMatches().keys shouldContainAll listOf("testId")
    }
})