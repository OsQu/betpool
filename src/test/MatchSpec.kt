import org.amshove.kluent.shouldEqual
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.it

class MatchSpec : Spek({
    it("toString returns the player names") {
        var match = Match("Ronnie", "Selby", Bank(listOf()))
        match.toString() shouldEqual "Ronnie - Selby"
    }
})
