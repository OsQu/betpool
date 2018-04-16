import org.amshove.kluent.shouldEqual
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it

class MatchSpec : Spek({
    describe("Match") {
        it("is instantiable something") {
            var match = Match("Ronnie", "Selby")
            match.toString() shouldEqual "Ronnie - Selby"
        }
    }
})
