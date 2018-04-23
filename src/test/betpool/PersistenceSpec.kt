import betpool.Action
import betpool.Competitor
import betpool.Odds
import betpool.Persistence
import org.amshove.kluent.shouldContain
import org.amshove.kluent.shouldEqual
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.it
import java.io.FileWriter
import java.util.*

class PersistenceSpec : Spek({
    it("serializes and deserializes actions to/from file") {
        FileWriter("/tmp/kotlinlog.log").close() // Truncates the file
        val persistence = Persistence("/tmp/kotlinlog.log")
        val action1 = Action.PlayerJoin("id1", "Sampo")

        val odds = Odds(mapOf("oddsId1" to Competitor("Ronnie", 150), "oddsId2" to Competitor("Selby", 200)))
        val action2 = Action.MatchNew(
                matchId = "testId1",
                matchName = "Ronnie v Selby",
                startDate = Date(),
                odds = odds
        )
        persistence.logAction(action1)
        persistence.logAction(action2)
        persistence.readActions() shouldContain action1
        persistence.readActions()[1].type shouldEqual Action.Type.MATCH_NEW
    }
})
