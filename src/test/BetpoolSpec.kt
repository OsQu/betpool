import org.amshove.kluent.shouldEqual
import org.amshove.kluent.shouldContainAll
import org.amshove.kluent.shouldThrow
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.it
import java.util.Date

class BetpoolSpec : Spek({
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
        betpool.applyAction(Action.MatchNew(matchId = "testId", athlete1Name = "Ronnie", athlete2Name = "Selby", startDate = Date()))
        betpool.getMatches().keys shouldContainAll listOf("testId")
    }
})