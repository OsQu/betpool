import org.amshove.kluent.shouldEqual
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.it
import java.util.Date

class BetSpec : Spek({
    it("is instantiable") {
        val match = Match(matchId = "id", athlete1Name = "Ronnie", athlete2Name = "Selby", startDate = Date())
        val bet = Bet(match, "test1")
        bet.match shouldEqual match
        bet.playerId shouldEqual  "test1"
    }
})
