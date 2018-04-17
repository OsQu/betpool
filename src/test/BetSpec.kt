import org.amshove.kluent.shouldEqual
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it

class BetSpec : Spek({
    describe("Match") {
        it("is instantiable something") {
            val match = Match("Ronnie", "Selby", Bank(listOf()))
            val bet = Bet(match)
            bet.match shouldEqual match
        }
    }
})
