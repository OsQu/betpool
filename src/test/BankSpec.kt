import org.amshove.kluent.shouldEqual
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it

class BankSpec : Spek({
    describe("Bank") {
        it("is instantiable") {
            Bank(listOf())
        }
    }
})