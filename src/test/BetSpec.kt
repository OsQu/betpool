import org.amshove.kluent.shouldEqual
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.it

class BetSpec : Spek({
    it("is instantiable") {
        val match = Match("Ronnie", "Selby")
        val bet = Bet(match, "test1")
        bet.match shouldEqual match
        bet.playerId shouldEqual  "test1"
    }
})
