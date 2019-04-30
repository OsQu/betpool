package betpool

import org.amshove.kluent.shouldEqual
import org.amshove.kluent.shouldThrow
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.it

class OddsSpec : Spek({
    it("scales odds") {
        val odds = Odds(
                mapOf("oddsId1" to Competitor(name = "Ronnie", odds = 150), "oddsId2" to Competitor(name = "Selby", odds = 200))
        )
        odds.getOdds() shouldEqual mapOf("oddsId1" to 175, "oddsId2" to 233)
    }

    it("raises if odds is less than 1") {
        val func = {
            Odds(
                mapOf("oddsId1" to Competitor(name = "Ronnie", odds = 99), "oddsId2" to Competitor(name = "Selby", odds = 200))
            )
        }
        func shouldThrow IllegalArgumentException::class
    }
})
