import org.amshove.kluent.shouldEqual
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.it
import java.util.Date

class MatchSpec : Spek({
    it("toString returns the player names") {
        var match = Match("id", "Ronnie", "Selby", Date())
        match.toString() shouldEqual "Ronnie - Selby"
    }
})
