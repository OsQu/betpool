import org.amshove.kluent.shouldEqual
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it

class HelloSpec : Spek({
    describe("awesome test") {
        it("does something") {
            "foo" shouldEqual "foo"
        }
    }
})