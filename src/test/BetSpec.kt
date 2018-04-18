import org.amshove.kluent.shouldEqual
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it

class BetSpec : Spek({
    it("is instantiable") {
        val match = Match("Ronnie", "Selby", Bank(listOf()))
        val bet = Bet(match)
        bet.match shouldEqual match
    }
})
