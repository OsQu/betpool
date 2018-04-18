import org.amshove.kluent.shouldEqual
import org.amshove.kluent.shouldThrow
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.it

class WinningsSpec : Spek({
    it("starts with empty data") {
        var winnings = Winnings()
        winnings.getData() shouldEqual HashMap<String, Int>()
    }

    it("addPlayer adds a player with zero winnings") {
        var winnings = Winnings()
        winnings.addPlayer("testId")
        winnings.getData() shouldEqual mapOf("testId" to 0)
    }

    it("mergeWinnings adds winnings") {
        var winnings = Winnings()
        var newWinnings = Winnings(HashMap(mapOf("p1" to 1, "p2" to -1)))
        winnings.merge(newWinnings)
        winnings.getData() shouldEqual mapOf("p1" to 1, "p2" to -1)
    }

    it("is impossible to create a Winning where sum of values is not zero") {
        val func = { Winnings(HashMap(mapOf("p1" to 1))) }
        func shouldThrow IllegalArgumentException::class
    }
})
